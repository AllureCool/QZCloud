package com.smile.qzclould.ui.transfer.viewmodel

import android.arch.lifecycle.MediatorLiveData
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.common.App
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.ui.cloud.bean.OfflineDownloadResult
import com.smile.qzclould.ui.cloud.bean.ParseUrlResultBean
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean
import com.smile.qzclould.ui.transfer.bean.FileDetailBean
import com.smile.qzclould.utils.DLog
import org.jetbrains.anko.doAsync

class TransferViewModel: BaseViewModel() {

    private val repo by lazy { HttpRepository() }
    private val mDao by lazy { App.getCloudDatabase()?.DirecotoryDao() }
    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }

    val offlineTaskList by lazy { MediatorLiveData<List<DownloadTaskBean.Task>>() }
    val localDownloadList by lazy { MediatorLiveData<List<Direcotory>>() }
    val parseUrlResult by lazy { MediatorLiveData<ParseUrlResultBean>() }
    val offlineDownloadResult by lazy { MediatorLiveData<OfflineDownloadResult>() }
    val fileDetail by lazy { MediatorLiveData<FileDetailBean>() }

    fun loadOfflineTask(page: Int, pageSize: Int, order: Int = 0) {
        repo.offlineDownloadList(page, pageSize, order)
                .subscribe({
                    if(it.success) {
                        offlineTaskList.value = it.data!!.list
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun loadLocalDownloadList() {
        doAsync {
            localDownloadList.postValue(mDao?.loadDirecotory())
        }
    }

    fun loadFileDetail(path: String, pos: Int) {
        repo.getFileDetail(path)
                .subscribe({
                    if(it.success) {
                        it.data?.position = pos
                        fileDetail.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun parseUrl(url: String) {
        repo.parseUrlS(url)
                .subscribe({
                    if(it.success) {
                        parseUrlResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(100, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun offlineDownloadStart(taskHash: String, savePath: String, copyFile: Array<Int> = arrayOf()) {
        repo.offlineDownloadStart(taskHash, savePath, copyFile)
                .subscribe({
                    if(it.success) {
                        offlineDownloadResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }
}