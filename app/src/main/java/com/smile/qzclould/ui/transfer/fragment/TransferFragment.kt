package com.smile.qzclould.ui.transfer.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.event.RefreshOfflineTaskListEvent
import com.smile.qzclould.ui.component.FileDeleteDialog
import com.smile.qzclould.ui.transfer.activity.OfflineFilePreviewActivity
import com.smile.qzclould.ui.transfer.adapter.DownloadTaskAdapter
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean
import com.smile.qzclould.ui.transfer.dialog.AddTaskDialog
import com.smile.qzclould.ui.transfer.viewmodel.TransferViewModel
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_home_transfer_download.*

class TransferFragment: BaseFragment() {

    companion object {
        const val LOCAL_DOWNLOAD = 0
        const val OFFLINE_DOWNLOAD = 1
    }

    private val mModel by lazy { ViewModelProviders.of(this).get(TransferViewModel::class.java) }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mAdapter by lazy { DownloadTaskAdapter() }
    private val mFileDeleteDialog by lazy { FileDeleteDialog() }
    private var mDeleteFile: DownloadTaskBean.Task? = null
    private var mDownloadType = LOCAL_DOWNLOAD
    private var mPage = 1


    override fun getLayoutId(): Int {
        return R.layout.frag_home_transfer_download
    }

    override fun initData() {
        mDownloadType = arguments?.getInt("download_type")!!
        when {
            mDownloadType == OFFLINE_DOWNLOAD -> loadOfflinTask(mPage)

        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mFlTask.visibility = View.VISIBLE
        mRvDownload.layoutManager = mLayoutManager
        mRvDownload.itemAnimator = null
        mAdapter.bindToRecyclerView(mRvDownload)
        mAdapter.setOnLoadMoreListener({loadOfflinTask(mPage)}, mRvDownload)
        mAdapter.setItemRemoveListener(object : DownloadTaskAdapter.OnItemRemoveListener {
            override fun onRemoved(file: DownloadTaskBean.Task?) {
            }
        })
        mRefreshLayout.setOnRefreshListener {
            mPage = 1
            loadOfflinTask(mPage)
        }
        mRefreshLayout.setColorSchemeColors(resources.getColor(R.color.color_green_2EC17C))
    }

    override fun initViewModel() {
        mModel.offlineTaskList.observe(this, Observer {
            mRefreshLayout.isRefreshing = false
            if(mPage == 1 && it!!.isEmpty()) {
                mAdapter.setEmptyView(R.layout.view_empty)
            }
            if(it!!.isEmpty()) {
                mAdapter.loadMoreEnd(true)
            } else {
                mAdapter.loadMoreComplete()
            }
            if(mPage == 1) {
                mAdapter.setNewData(it)
            } else {
                mAdapter.addData(it)
            }
            mPage++
        })

        mModel.removeResult.observe(this, Observer {
            mAdapter.data.remove(mDeleteFile)
            mAdapter.notifyDataSetChanged()
            if(mAdapter.data.isEmpty()) {
                mAdapter.setEmptyView(R.layout.view_empty)
            }
        })
    }

    override fun initListener() {
        mFlTask.setOnClickListener {
            val addTaskDialog = AddTaskDialog()
            addTaskDialog.show(childFragmentManager, "add_task_dialog")
        }

        mFileDeleteDialog.setOnDialogClickListener(object : FileDeleteDialog.OnDialogClickListener {
            override fun onDeleteClick() {

                mModel.removeFile(mDeleteFile!!.taskId)
            }
        })

        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            mDeleteFile = adapter.getItem(position) as DownloadTaskBean.Task

            if(!mFileDeleteDialog.isAdded) {
                mFileDeleteDialog.showNow(childFragmentManager, "file_delete_dialog")
            }
            return@setOnItemLongClickListener true
        }

        mAdapter.setOnItemClickListener { adapter, view, position ->
            val itemData = adapter.data[position] as DownloadTaskBean.Task
            val bundle = Bundle()
            bundle.putString("file_path", itemData.savePath)
            jumpActivity(OfflineFilePreviewActivity::class.java, bundle)
//            if(itemData.mime.contains(Constants.MIME_VIDEO)) {
//                val bundle = Bundle()
//                bundle.putBoolean("isLocal", false)
//                bundle.putString("path", itemData.filePath)
//                bundle.putBoolean("hasPreview", false)
//                jumpActivity(PlayerActivity::class.java, bundle)
//            }
        }
    }

    override fun initEvent() {
        RxBus.toObservable(RefreshOfflineTaskListEvent::class.java)
                .subscribe {
                    mPage = 1
                    loadOfflinTask(mPage)
                }
                .autoDispose()
    }

    private fun loadOfflinTask(page: Int) {
        mModel.loadOfflineTask(page, 3)

    }
}