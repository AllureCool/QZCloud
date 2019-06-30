package com.smile.qzclould.ui.preview

import android.arch.lifecycle.MediatorLiveData
import com.smile.qielive.common.mvvm.BaseViewModel
import com.smile.qielive.common.mvvm.ErrorStatus
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.ui.preview.picture.PictureBean
import com.smile.qzclould.ui.preview.picture.PictureBeanV2
import com.smile.qzclould.ui.preview.player.bean.VideoDetailBean
import com.smile.qzclould.ui.transfer.bean.FileDetailBean

class PreviewViewModel: BaseViewModel() {
    private val repo by lazy { HttpRepository() }

    val MediaInfoResult by lazy { MediatorLiveData<VideoDetailBean>() }
    val pictureInfoResult by lazy { MediatorLiveData<PictureBeanV2>() }
    val fileDetail by lazy { MediatorLiveData<FileDetailBean>() }
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

    fun getPictureInfo(path: String) {
        repo.getPictureInfoV2(path)
                .subscribe({
                    if(it.success) {
                        pictureInfoResult.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }

    fun loadFileDetail(path: String) {
        repo.getFileDetailV2(path)
                .subscribe({
                    if (it.success) {
                        fileDetail.value = it.data
                    } else {
                        errorStatus.value = ErrorStatus(it.status, it.message)
                    }
                }, {
                    errorStatus.value = ErrorStatus(100, it.message)
                })
                .autoDispose()
    }
}