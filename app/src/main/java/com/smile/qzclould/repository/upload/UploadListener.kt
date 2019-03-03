package com.smile.qzclould.repository.upload

import java.util.concurrent.CountDownLatch

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
class UploadListener: Runnable {
    private var mDownLatch: CountDownLatch? = null
    private var mListener: OnAllThreadResultListener? = null

    constructor(countDownLatch: CountDownLatch?, listener: OnAllThreadResultListener) {
        mDownLatch = countDownLatch
        mListener = listener
    }

    override fun run() {
        try {
            mDownLatch?.await()
            mListener?.onSuccess()
        } catch (e: InterruptedException) {
            mListener?.onFailed()
        }
    }
}