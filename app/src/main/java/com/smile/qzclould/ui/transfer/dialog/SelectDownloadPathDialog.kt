package com.smile.qzclould.ui.transfer.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.event.SelectDownloadPathEvent
import com.smile.qzclould.ui.transfer.adapter.DownloadPathSelectAdapter
import com.smile.qzclould.ui.transfer.viewmodel.TransferViewModel
import com.smile.qzclould.utils.RxBus
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_select_download_path.*

class SelectDownloadPathDialog: BaseDialogFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(TransferViewModel::class.java) }
    private val mAdapter by lazy { DownloadPathSelectAdapter() }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private var mSelectPath = "/"
    private var mFilePath = "/"
    private var mBackPath = mutableListOf<String>()
    private val mPageSize = 20
    private var mPage = 1

    override fun setLayoutId(): Int {
        return R.layout.dialog_select_download_path
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogFragmentStyle)
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.BOTTOM)
        mWindow.setWindowAnimations(R.style.MyBottomDialog)
        mWindow.setLayout(mWidth, mHeight * 2 / 3)
    }

    override fun initView() {
        mRvFolder.layoutManager = mLayoutManager
        mAdapter.bindToRecyclerView(mRvFolder)
        mAdapter.setOnLoadMoreListener({ listFileByPath() }, mRvFolder)
        mAdapter.setOnCheckListener(object : DownloadPathSelectAdapter.OnCheckListener {
            override fun onChecked(position: Int, item: Direcotory?) {
                mSelectPath = item!!.path
            }

            override fun onItemClick(position: Int, item: Direcotory?) {
                mBackPath.add(mFilePath)
                mFilePath = item!!.path
                mPage = 1
                listFileByPath()
            }

            override fun onItemLongClick(position: Int, item: Direcotory?) {

            }
        })

        mIvBack.setOnClickListener {
            if(!mBackPath.isEmpty()) {
                mFilePath = mBackPath[mBackPath.lastIndex]
                mPage = 1
                mBackPath.removeAt(mBackPath.lastIndex)
                listFileByPath()
            } else {
                dismiss()
            }
        }

        mBtnConfirm.setOnClickListener {
            if(mSelectPath == "/") {
                Toasty.normal(mActivity, "请选择下载路径").show()
            } else {
                RxBus.post(SelectDownloadPathEvent(mSelectPath))
                dismiss()
            }
        }

        initViewModel()

        listFileByPath()
    }

    private fun initViewModel() {

        mModel.folderListResult.observe(this, Observer {
            mSelectPath = "/"
            if (mPage == 1 && it!!.isEmpty()) {
                mAdapter.setNewData(it)
                mAdapter.setEmptyView(R.layout.view_empty)
            } else {
                if (it!!.isEmpty()) {
                    mAdapter.loadMoreEnd(true)
                } else {
                    mAdapter.loadMoreComplete()
                }
                if (mPage == 1) {
                    mAdapter.setNewData(it)
                } else {
                    mAdapter.addData(it!!)
                }
                mPage++
            }
        })
    }

    private fun listFileByPath() {
        mModel.listFolderByPath(mFilePath, mPage, mPageSize, 0)
    }
}