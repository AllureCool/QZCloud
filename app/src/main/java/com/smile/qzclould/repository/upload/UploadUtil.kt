package com.smile.qzclould.repository.upload

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.smile.qzclould.db.UploadFileEntity
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
class UploadUtil {
    private val THREAD_PROGRESS_CODE = 100//线程进度回调
    private val THREAD_FINISH_CODE = 101//线程完成
    private val THREAD_INTERRUPT_CODE = 102//线程被中断
    private val THREAD_ALL_SUCCESS_CODE = 103//所有线程完成
    private val THREAD_ALL_FAILED_CODE = 104//所有线程执行失败
    private val THREAD_PERCENT = "THREAD_PERCENT"
    private val THREAD_POSITION = "THREAD_POSITION"

    private var mThreadCount = 0//任务数量
    private var mThreadCore = 3//线程池核心数

    private var mExecutor: ExecutorService? = null//线程池
    private var mDownLatch: CountDownLatch? = null//计数器

    private var mUploadListener: OnUploadListener? = null
    private var mHandler: UploadHandler? = null

    constructor() {
        init()
    }

    constructor(threadCore: Int) {
        mThreadCore = threadCore
        init()
    }

    fun setOnUploadListener(uploadListener: OnUploadListener) {
        this.mUploadListener = uploadListener
    }

    fun init() {
        mHandler = UploadHandler(this)
    }

    fun shutDownNow() {
        mExecutor?.shutdownNow()//中断所有线程的执行
    }

    fun submitAll(files: List<UploadFileEntity>, totalFiles: List<UploadFileEntity>, threadCount: Int) {
        mThreadCount += threadCount
        if(mExecutor == null) {
            mDownLatch = CountDownLatch(mThreadCount)
            mExecutor = Executors.newFixedThreadPool(mThreadCore + 1)
            mExecutor?.submit(UploadListener(mDownLatch, object: OnAllThreadResultListener {
                override fun onSuccess() {
                    mHandler?.sendEmptyMessage(THREAD_ALL_SUCCESS_CODE)
                }

                override fun onFailed() {
                    mHandler?.sendEmptyMessage(THREAD_ALL_FAILED_CODE)
                }
            }))
        }

        for (i in 0 until files.size) {
            val bundle = Bundle()
            mExecutor?.submit(UploadFile(mDownLatch, files[i], object: OnThreadResultListener {
                override fun onProgressChange(percent: Int) {
                    bundle.putInt(THREAD_POSITION, totalFiles.indexOf(files[i]))
                    bundle.putInt(THREAD_PERCENT, percent)
                    Message.obtain(mHandler, THREAD_PROGRESS_CODE, bundle).sendToTarget()
                }

                override fun onFinish() {
                    bundle.putInt(THREAD_POSITION, totalFiles.indexOf(files[i]))
                    Message.obtain(mHandler, THREAD_FINISH_CODE, bundle).sendToTarget()
                }

                override fun onInterrupted() {
                    bundle.putInt(THREAD_POSITION, totalFiles.indexOf(files[i]))
                    Message.obtain(mHandler, THREAD_INTERRUPT_CODE, bundle).sendToTarget()
                }
            }))
        }
//        mExecutor?.shutdown() //关闭线程池
    }

    private class UploadHandler constructor(`object`: UploadUtil) : Handler(Looper.getMainLooper()) {
        //静态内部类+弱引用防止内存泄漏
        private val weakReference: WeakReference<UploadUtil>

        init {
            weakReference = WeakReference(`object`)
        }//执行在UI线程

        override fun handleMessage(msg: Message) {
            val uploadUtil = weakReference.get()
            if (uploadUtil != null && msg.obj is Bundle) {
                val data = msg.obj as Bundle
                val position: Int
                val percent: Int

                when (msg.what) {
                    weakReference.get()?.THREAD_PROGRESS_CODE -> {
                        position = data.getInt(weakReference.get()?.THREAD_POSITION)
                        percent = data.getInt(weakReference.get()?.THREAD_PERCENT)
                        uploadUtil.mUploadListener?.onThreadProgressChange(position, percent)
                    }
                    weakReference.get()?.THREAD_FINISH_CODE -> {
                        position = data.getInt(weakReference.get()?.THREAD_POSITION)
                        uploadUtil.mUploadListener?.onThreadFinish(position)
                    }
                    weakReference.get()?.THREAD_INTERRUPT_CODE -> {
                        position = data.getInt(weakReference.get()?.THREAD_POSITION)
                        uploadUtil.mUploadListener?.onThreadInterrupted(position)
                    }
                    weakReference.get()?.THREAD_ALL_SUCCESS_CODE -> uploadUtil.mUploadListener?.onAllSuccess()
                    weakReference.get()?.THREAD_ALL_FAILED_CODE -> uploadUtil.mUploadListener?.onAllFailed()

                    else -> {
                    }
                }
            }
        }
    }
}