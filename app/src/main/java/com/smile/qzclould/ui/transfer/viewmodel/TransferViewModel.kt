package com.smile.qzclould.ui.transfer.viewmodel

import android.arch.lifecycle.MediatorLiveData
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.repository.requestbody.OfflineAddBody
import com.smile.qzclould.ui.cloud.bean.*
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean
import com.smile.qzclould.ui.transfer.bean.FileDetailBean
import com.smile.qzclould.ui.transfer.bean.OfflineListBean
import com.smile.qzclould.utils.DLog
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class TransferViewModel : BaseViewModel() {

    private val repo by lazy { HttpRepository() }
    private val mDao by lazy { App.getCloudDatabase()?.DirecotoryDao() }
    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }

    val offlineTaskList by lazy { MediatorLiveData<List<DownloadTaskBean.Task>>() }
    val localDownloadList by lazy { MediatorLiveData<List<Direcotory>>() }
    val parseUrlResult by lazy { MediatorLiveData<ParseUrlResultBean>() }
    val offlineParseRsp by lazy { MediatorLiveData<List<OfflineParseBean>>() }
    val offlineDownloadResult by lazy { MediatorLiveData<OfflineDownloadResult>() }
    val fileDetail by lazy { MediatorLiveData<FileDetailBean>() }
    val removeResult by lazy { MediatorLiveData<String>() }
    val folderListResult by lazy { MediatorLiveData<List<Direcotory>>() }
    val offlineResp by lazy { MediatorLiveData<OfflineAddResultBean>() }
    val offlineListResp by lazy { MediatorLiveData<OfflineListBean>() }

    fun loadOfflineTask(start: Int, listSize: Int) {
        repo.offlineDownloadListV2(start, listSize)
                .subscribe({
                    if (it.success) {
                        offlineListResp.value = it.data!!
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
            try {
                val cursor = mDao?.loadDirecotory()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            localDownloadList.postValue(mDao?.loadDirecotory())
        }
    }

    fun loadFileDetail(path: String, pos: Int) {
        repo.getFileDetailV2(path)
                .subscribe({
                    if (it.success) {
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
        repo.parseUrlSV2(url)
                .subscribe({
                    if (it.success) {
                        offlineParseRsp.postValue(it.data)
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
                    if (it.success) {
                        offlineDownloadResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun startOfflinDownload(path: String, tasks: List<OfflineAddBody.OfflineTask>) {
        repo.offlineDownloadV2(path, tasks)
                .subscribe({
                    if (it.success) {
                        offlineResp.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun listFolderByPath(path: String, page: Int, pageSize: Int, orderBy: Int, type: Int = -1) {
        repo.listFileByPathV2(path, page, pageSize, orderBy, type)
                .subscribe({
                    if (it.success) {
//                        if (filterList(it.data?.list)!!.isNotEmpty()) {
                        folderListResult.value = filterList(it.data?.list)
//                        }
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    private fun filterList(list: List<Direcotory>?): List<Direcotory>? {
        val filterList = mutableListOf<Direcotory>()
        for (item in list!!) {
            if (item.mime == Constants.MIME_FOLDER) {
                filterList.add(item)
            }
        }
        return filterList
    }

    fun removeFile(taskId: String) {
        repo.removeOfflineFile(taskId)
                .subscribe({
                    if (it.success) {
                        removeResult.value = it.data
                    }
                }, {

                })
                .autoDispose()
    }
}