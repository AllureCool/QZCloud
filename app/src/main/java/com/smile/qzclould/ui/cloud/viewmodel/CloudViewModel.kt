package com.smile.qzclould.ui.cloud.viewmodel

import android.arch.lifecycle.MediatorLiveData
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.ui.cloud.bean.DirecotoryBean
import com.smile.qzclould.ui.cloud.bean.ParseUrlResultBean

class CloudViewModel : BaseViewModel() {
    val repo by lazy { HttpRepository() }
    val createDirectoryResult by lazy { MediatorLiveData<DirecotoryBean>() }
    val listFileResult by lazy { MediatorLiveData<List<DirecotoryBean>>() }
    val parseUrlResult by lazy { MediatorLiveData<ParseUrlResultBean>() }
    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }

    fun createDirectory(name: String, parentUUid: String = "") {
        repo.createDirectory(name, parentUUid)
                .subscribe({
                    if(it.success) {
                        createDirectoryResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun listFile( parent: String, path: String, start: Int, size: Int, recycle: Int, mime: String, orderBy: Int, type: Int) {
        repo.listFile(parent, path, start, size, recycle, mime, orderBy, type)
                .subscribe({
                    if(it.success) {
                        listFileResult.value = it.data
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

                }, {

                })
                .autoDispose()
    }
}
