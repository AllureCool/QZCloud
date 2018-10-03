package com.smile.qzclould.ui.preview.pdf

import android.arch.lifecycle.MediatorLiveData
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.repository.HttpRepository

class PdfViewModel: BaseViewModel() {

    private val repo by lazy { HttpRepository() }

    val MediaInfoResult by lazy { MediatorLiveData<String>() }

    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }

    fun getPdfInfo(path: String) {
        repo.getPdfInfo(path)
                .subscribe({
                    if(it.success) {
                        MediaInfoResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }
}