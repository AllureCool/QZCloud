package com.smile.qzclould.ui.task

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.event.FileDownloadCompleteEvent
import com.smile.qzclould.ui.task.adapter.FileDownloadCompleteAdapter
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_home_third.*
import java.io.File

class HomeThirdFragment: BaseFragment() {

    private val path = FileDownloadUtils.getDefaultSaveRootPath() + File.separator
    private val mAdapter by lazy { FileDownloadCompleteAdapter() }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }

    private fun loadAlreadyDownloadFiles(): MutableList<File> {
        val file = File(path)
        val list = mutableListOf<File>()
        for (item in file.listFiles()) {
            list.add(item)
        }
        return list
    }

    private fun refreshList() {
        val fileList = loadAlreadyDownloadFiles()

        if(fileList.isEmpty()) {
            mAdapter.setEmptyView(R.layout.view_empty)
        } else {
            mAdapter.setNewData(fileList)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_home_third
    }

    override fun initView(savedInstanceState: Bundle?) {
        mRvFile.layoutManager = mLayoutManager
        mAdapter.bindToRecyclerView(mRvFile)
        refreshList()
    }

    override fun initEvent() {
        RxBus.toObservable(FileDownloadCompleteEvent::class.java)
                .subscribe {
                    refreshList()
                }
                .autoDispose()
    }

}