package com.smile.qzclould.ui.player

import android.arch.lifecycle.MediatorLiveData
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.repository.HttpRepository

class MediaViewModel: BaseViewModel() {
    private val repo by lazy { HttpRepository() }

    val MediaInfoResult by lazy { MediatorLiveData<VideoDetailBean>() }

    val errorStatus by lazy { MediatorLiveData<ErrorStatus>() }

    fun getMediaInfo(path: String) {
        repo.getMediaInfo(path)
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