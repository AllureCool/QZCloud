package com.smile.qzclould.ui.transfer.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.event.FileDownloadCompleteEvent
import com.smile.qzclould.event.FileDownloadEvent
import com.smile.qzclould.ui.component.FileDeleteDialog
import com.smile.qzclould.ui.transfer.adapter.LocalDownloadAdapter
import com.smile.qzclould.ui.transfer.viewmodel.TransferViewModel
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_home_transfer_download.*
import org.jetbrains.anko.doAsync
import java.io.File

class LocalDownloadFragment : BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(TransferViewModel::class.java) }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mAdapter by lazy { LocalDownloadAdapter() }
    private val mFileDeleteDialog by lazy { FileDeleteDialog() }
    private var mDeleteFile: Direcotory? = null
    private var mShouldDownloadNow: Boolean = false

    override fun getLayoutId(): Int {
        return R.layout.frag_home_transfer_download
    }

    override fun initData() {
        mModel.loadLocalDownloadList()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mRvDownload.layoutManager = mLayoutManager
//        mRvDownload.itemAnimator =NoAlphaItemAnimator()
        mRvDownload.itemAnimator = null

        mAdapter.setHasStableIds(true)
        mAdapter.bindToRecyclerView(mRvDownload)
        mAdapter.setEmptyView(R.layout.view_empty)
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            mDeleteFile = adapter.getItem(position) as Direcotory
            if(!mFileDeleteDialog.isAdded) {
                mFileDeleteDialog.showNow(childFragmentManager, "file_delete_dialog")
            }
            return@setOnItemLongClickListener true
        }

        mRefreshLayout.isEnabled = false
    }

    override fun initListener() {
        mFileDeleteDialog?.setOnDialogClickListener(object : FileDeleteDialog.OnDialogClickListener {
            override fun onDeleteClick() {
                FileDownloader.getImpl().clear(mDeleteFile!!.taskId, FileDownloadUtils.getDefaultSaveRootPath() + File.separator + mDeleteFile?.fileDetail?.name)
                doAsync {
                    val dao = App.getCloudDatabase()?.DirecotoryDao()
                    dao?.deleteDirecotory(mDeleteFile!!)
                }
                if(mAdapter.data.contains(mDeleteFile)) {
                    val index = mAdapter.data.indexOf(mDeleteFile)
                    mAdapter.data.removeAt(index)
                    mAdapter.notifyItemRemoved(index)
                }
                RxBus.post(FileDownloadCompleteEvent())
            }
        })
    }

    override fun initViewModel() {
        mModel.localDownloadList.observe(this, Observer {
            mAdapter.setNewData(it)
        })
    }

    override fun initEvent() {
        RxBus.toObservable(FileDownloadEvent::class.java)
                .subscribe {
                    mShouldDownloadNow = it.shouldDownloadNow
                    mModel.loadLocalDownloadList()
                }
                .autoDispose()

    }
}