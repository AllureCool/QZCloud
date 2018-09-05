package com.smile.qzclould.ui.transfer.viewmodel

import android.arch.lifecycle.MediatorLiveData
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean

class TransferViewModel: BaseViewModel() {

    private val repo by lazy { HttpRepository() }
    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }

    val offlineTaskList by lazy { MediatorLiveData<List<DownloadTaskBean.Task>>() }

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
}