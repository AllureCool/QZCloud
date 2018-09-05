package com.smile.qzclould.ui.transfer.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.ui.transfer.adapter.DownloadTaskAdapter
import com.smile.qzclould.ui.transfer.viewmodel.TransferViewModel
import kotlinx.android.synthetic.main.frag_home_transfer_download.*

class TransferFragment: BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(TransferViewModel::class.java) }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mAdapter by lazy { DownloadTaskAdapter() }


    override fun getLayoutId(): Int {
        return R.layout.frag_home_transfer_download
    }

    override fun initData() {
        mModel.loadOfflineTask(1, 10)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mRvDownload.layoutManager = mLayoutManager
        mAdapter.bindToRecyclerView(mRvDownload)
    }

    override fun initViewModel() {
        mModel.offlineTaskList.observe(this, Observer {
            mAdapter.setNewData(it)
        })
    }
}