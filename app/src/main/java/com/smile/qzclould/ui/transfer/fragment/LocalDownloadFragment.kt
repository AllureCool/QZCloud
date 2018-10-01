package com.smile.qzclould.ui.transfer.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.event.FileDownloadEvent
import com.smile.qzclould.ui.transfer.adapter.LocalDownloadAdapter
import com.smile.qzclould.ui.transfer.viewmodel.TransferViewModel
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_home_transfer_download.*

class LocalDownloadFragment : BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(TransferViewModel::class.java) }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mAdapter by lazy { LocalDownloadAdapter() }
    private var mShouldDownloadNow: Boolean = false

    override fun getLayoutId(): Int {
        return R.layout.frag_home_transfer_download
    }

    override fun initData() {
        mModel.loadLocalDownloadList()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mRvDownload.layoutManager = mLayoutManager
        mRvDownload.itemAnimator = null
        mAdapter.setHasStableIds(true)
        mAdapter.bindToRecyclerView(mRvDownload)
        mAdapter.setEmptyView(R.layout.view_empty)
    }

    override fun initViewModel() {
        mModel.localDownloadList.observe(this, Observer {
            mAdapter.setNewData(it)
            if (!it!!.isEmpty() && mShouldDownloadNow) {
//                mAdapter.getDownloadInfo(it[0].path, mRvDownload.get)
            }

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