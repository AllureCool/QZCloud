package com.smile.qzclould.repository.upload

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
interface OnUploadListener {
    fun onAllSuccess()
    fun onAllFailed()
    fun onThreadProgressChange(position: Int, percent: Int)
    fun onThreadFinish(position: Int)
    fun onThreadInterrupted(position: Int)
}