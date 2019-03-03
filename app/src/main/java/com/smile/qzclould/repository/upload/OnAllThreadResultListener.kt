package com.smile.qzclould.repository.upload

/**
 * Created by wangzhg on 2019/3/2
 * Describe: 线程池中所有线程执行结果的监听
 */
interface OnAllThreadResultListener {
    fun onSuccess()
    fun onFailed()
}