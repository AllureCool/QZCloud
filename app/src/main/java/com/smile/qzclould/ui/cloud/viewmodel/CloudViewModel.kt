package com.smile.qzclould.ui.cloud.viewmodel

import android.arch.lifecycle.MediatorLiveData
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.repository.requestbody.MoveFileBodyV2
import com.smile.qzclould.repository.requestbody.PathArrayBodyV2
import com.smile.qzclould.ui.cloud.bean.ParseUrlResultBean

class CloudViewModel : BaseViewModel() {
    val repo by lazy { HttpRepository() }
    val createDirectoryResult by lazy { MediatorLiveData<Direcotory>() }
    val listFileResult by lazy { MediatorLiveData<List<Direcotory>>() }
    val removeResult by lazy { MediatorLiveData<String>() }
    val moveFileResult by lazy { MediatorLiveData<String>() }
    val copyFileResult by lazy { MediatorLiveData<String>() }
    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }

    fun createDirectory(name: String, parentUUid: String = "") {
        repo.createDirectoryV2(name, parentUUid)
                .subscribe({
                    if (it.success) {
                        createDirectoryResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun listFileByPath(path: String, page: Int, pageSize: Int, orderBy: Int, type: Int = -1) {
        repo.listFileByPathV2(path, page, pageSize, orderBy, type)
                .subscribe({
                    if (it.success) {
                        val fileList = mutableListOf<Direcotory>()
                        for (item in it.data!!.list) {
                            if(!item.locking) {
                                fileList.add(item)
                            }
                        }
                        listFileResult.value = fileList
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun removeFile(path: List<PathArrayBodyV2.Source>) {
        repo.removeFileV2(path)
                .subscribe({
                    if (it.success) {
                        removeResult.value = it.data
                    }
                }, {

                })
                .autoDispose()
    }

    fun moveFile(path: List<MoveFileBodyV2.Source>, destPath: MoveFileBodyV2.Destination) {
        repo.moveFileV2(path, destPath)
                .subscribe({
                    if(it.success) {
                        moveFileResult.value = it.data
                    }
                }, {

                })
                .autoDispose()
    }

    fun copyFile(path: List<MoveFileBodyV2.Source>, destPath: MoveFileBodyV2.Destination) {
        repo.copyFileV2(path, destPath)
                .subscribe({
                    if(it.success) {
                        copyFileResult.value = it.data
                    }
                }, {

                })
                .autoDispose()
    }
}
