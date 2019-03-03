package com.smile.qzclould.repository.upload

import android.util.Log
import android.widget.Toast
import com.chinanetcenter.wcs.android.api.FileUploader
import com.chinanetcenter.wcs.android.api.ParamsConf
import com.chinanetcenter.wcs.android.entity.OperationMessage
import com.chinanetcenter.wcs.android.internal.UploadFileRequest
import com.chinanetcenter.wcs.android.listener.FileUploaderListener
import com.smile.qzclould.common.App
import com.smile.qzclould.db.UploadFileEntity
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.ui.transfer.bean.UploadFileResponeBean
import java.util.concurrent.CountDownLatch
import com.smile.qzclould.utils.EncodeUtils.getFileMD5
import okhttp3.Callback
import org.json.JSONObject
import java.io.File
import java.util.*
import java.io.IOException
import java.lang.Exception


/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
class UploadFile: Runnable {

    private var mDownLatch: CountDownLatch? = null
    private var mFile: UploadFileEntity? = null
    private var mListener: OnThreadResultListener? = null
    private var isFinished = false
    private val mRandom: Random//随机数 模拟上传
    private val repo by lazy { HttpRepository() }

    constructor(downLatch: CountDownLatch?, file: UploadFileEntity?, listener: OnThreadResultListener?) {
        mDownLatch = downLatch
        mFile = file
        mListener = listener
        mRandom = Random()
    }

    //上传文件的逻辑
    override fun run() {
//        while (percent <= 200) {
//            mFile?.uploadPercent = percent
//            mFile?.status = 1
//            mListener?.onProgressChange(percent)
//            percent += 1
//            Thread.sleep((mRandom.nextInt(60) + 30).toLong())//模拟延迟
//        }
//        mListener?.onFinish()//顺利完成
//        //完成一个线程工作的数目减去1
//        this.mDownLatch?.countDown()
        val file = File(mFile?.filePath)
        val md5 = getFileMD5(file)
        repo.uploadFile(mFile?.fileName!!, md5, "/", "/")
                .subscribe({
                    initUploadParams(it.data!!)
                    uploadFile(it.data!!)
                }, {
                    isFinished = true
                    mListener?.onInterrupted()
                    mDownLatch?.countDown()
                })
        while (!isFinished) {
            Thread.sleep(10)
        }
        mDownLatch?.countDown()
    }

    private fun initUploadParams(data: UploadFileResponeBean) {
        FileUploader.setUploadUrl(data.uploadUrl)
        val conf = ParamsConf()
        conf.fileName = mFile?.fileName
        conf.keyName = mFile?.fileName
        FileUploader.setParams(conf)
    }

    private fun uploadFile(data: UploadFileResponeBean) {
        val file = File(mFile?.filePath)
        val token = data.token
        val callbackBody = HashMap<String, String>()
        try {
            FileUploader.upload(App.instance, token, file, callbackBody, object : FileUploaderListener() {
                override fun onSuccess(status: Int, responseJson: JSONObject?) {
                    isFinished = true
                    mFile?.status = 2
                    mListener?.onFinish()
                }

                override fun onFailure(operationMessage: OperationMessage?) {
                    isFinished = true
                    mFile?.status = 3
                    mListener?.onInterrupted()
                }

                override fun onProgress(request: UploadFileRequest?, currentSize: Long, totalSize: Long) {
                    val percent = (currentSize.toFloat() / totalSize * 100).toInt()
                    mFile?.status = 1
                    mFile?.uploadPercent = percent
                    mListener?.onProgressChange(percent)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}