package com.smile.qzclould.repository.upload

/**
 * Created by wangzhg on 2019/3/2
 * Describe: 单个下载线程的执行结果监听
 */
interface OnThreadResultListener {
    fun onProgressChange(percent: Int) //进度变化回调
    fun onFinish() //线程执行完成回调
    fun onInterrupted() //线程被中断的回调
}