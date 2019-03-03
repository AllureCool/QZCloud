package com.smile.qzclould.ui.transfer.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.db.UploadFileEntity
import com.smile.qzclould.event.UploadFileEvent
import com.smile.qzclould.repository.upload.OnUploadListener
import com.smile.qzclould.repository.upload.UploadUtil
import com.smile.qzclould.ui.transfer.adapter.UploadAdapter
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_upload.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
class UploadFileFragment: BaseFragment() {

    private val mAdapter by lazy { UploadAdapter() }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mUploadUtil by lazy { UploadUtil() }
    private val mDao by lazy { App.getCloudDatabase()?.UploadFileDao() }

    override fun getLayoutId(): Int {
        return R.layout.frag_upload
    }

    override fun initData() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        rv_upload.layoutManager = mLayoutManager
        rv_upload.itemAnimator = null
        mAdapter.bindToRecyclerView(rv_upload)
        doAsync {
            val fileList = mDao?.loadDirecotory()
            uiThread {
                if(fileList != null) {
                    mAdapter.setNewData(fileList)
                    val uploadList = mutableListOf<UploadFileEntity>()
                    for(file in fileList) {
                        if(file.status != 2) {
                            uploadList.add(file)
                            mUploadUtil.submitAll(uploadList, mAdapter.data, uploadList.size)
                        }
                    }
                }
            }
        }
    }

    override fun initEvent() {
        RxBus.toObservable(UploadFileEvent::class.java)
                .subscribe {
                    val fileList = mutableListOf<UploadFileEntity>()
                    for (file in it.fileList) {
                        val fileBean = UploadFileEntity(file, file, 0, 0)
                        fileList.add(fileBean)
                    }
                    if(mAdapter.data.isEmpty()) {
                        mAdapter.setNewData(fileList)
                    } else {
                        mAdapter.addData(fileList)
                    }
                    doAsync {
                        mDao?.saveUploadFiles(fileList)
                    }
                    mUploadUtil.submitAll(fileList,mAdapter.data,fileList.size)
                }
                .autoDispose()
    }

    override fun initListener() {
        mUploadUtil.setOnUploadListener(object: OnUploadListener {

            override fun onAllSuccess() {

            }

            override fun onAllFailed() {

            }

            override fun onThreadProgressChange(position: Int, percent: Int) {
                mAdapter.notifyItemChanged(position)
            }

            override fun onThreadFinish(position: Int) {
                mAdapter.notifyItemChanged(position)
                doAsync {
                    mDao?.updateFiles(mAdapter.data[position])
                }
            }

            override fun onThreadInterrupted(position: Int) {
                mAdapter.notifyItemChanged(position)
                doAsync {
                    mDao?.updateFiles(mAdapter.data[position])
                }
            }
        })
    }
}