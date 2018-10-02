package com.smile.qzclould.ui.transfer.adapter

import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadSampleListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.event.FileDownloadCompleteEvent
import com.smile.qzclould.manager.TasksManager
import com.smile.qzclould.ui.transfer.bean.FileDetailBean
import com.smile.qzclould.ui.transfer.viewmodel.TransferViewModel
import com.smile.qzclould.utils.DLog
import com.smile.qzclould.utils.RxBus
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

class LocalDownloadAdapter : BaseQuickAdapter<Direcotory, BaseViewHolder> {
    private val mViewModel by lazy { TransferViewModel() }
    lateinit var observer: Observer<FileDetailBean>

    private val mTaskDownloadListener = object : FileDownloadSampleListener() {
        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            super.pending(task, soFarBytes, totalBytes)
            val itemData = task?.tag as Direcotory
            itemData?.downloadStatus = 1
            notifyItemChanged(data.indexOf(itemData))
            updateFileInfo(itemData)
        }

        override fun started(task: BaseDownloadTask?) {
            super.started(task)
            val itemData = task?.tag as Direcotory
            itemData?.downloadStatus = 1
            itemData?.taskId = task.id
            notifyItemChanged(data.indexOf(itemData))
            updateFileInfo(itemData)
        }

        override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes)
        }

        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            super.progress(task, soFarBytes, totalBytes)
            val itemData = task?.tag as Direcotory
            itemData?.totalSize = totalBytes
            itemData?.downloadSize = soFarBytes

            val percent = soFarBytes / totalBytes.toFloat()
            itemData?.downProgress = (percent * 100).toInt()

            itemData?.downloadStatus = 1
            notifyItemChanged(data.indexOf(itemData))
            updateFileInfo(itemData)
        }

        override fun error(task: BaseDownloadTask?, e: Throwable?) {
            super.error(task, e)


            val itemData = task?.tag as Direcotory
//            mViewModel.loadFileDetail(itemData!!.path, task?.tag as Int)
            itemData?.downloadStatus = 3
            notifyItemChanged(data.indexOf(itemData))
            updateFileInfo(itemData)
        }

        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            super.paused(task, soFarBytes, totalBytes)
            val itemData = task?.tag as Direcotory
            itemData?.downloadStatus = 2
            notifyItemChanged(data.indexOf(itemData))
            updateFileInfo(itemData)
        }

        override fun completed(task: BaseDownloadTask?) {
            super.completed(task)
            val itemData = task?.tag as Direcotory
            data.remove(itemData)
            notifyItemRemoved(data.indexOf(itemData))
            deleteFile(itemData)
            RxBus.post(FileDownloadCompleteEvent())
        }
    }

    constructor() : super(R.layout.item_local_download) {
        initViewModel()
    }


    override fun setNewData(data: List<Direcotory>?) {
        super.setNewData(data)

        for(item in data!!) {
            when (item.downloadStatus) {
                1 -> {
                    if(item.fileDetail != null) {
                        startDownload(item!!)
                    }
                }
            }
        }
    }

    private fun deleteFile(fileInfo: Direcotory?) {
        if(fileInfo == null) {
            return
        }
        doAsync {
            val dao = App.getCloudDatabase()?.DirecotoryDao()
            dao?.deleteDirecotory(fileInfo)
        }
    }

    private fun updateFileInfo(fileInfo: Direcotory?) {
        if(fileInfo == null) {
            return
        }
        doAsync {
            val dao = App.getCloudDatabase()?.DirecotoryDao()
            dao?.updateDirecotoryInfo(fileInfo)
        }
    }

    private fun initViewModel() {
        observer = Observer {
            data[it!!.position].fileDetail = it
            startDownload(data[it!!.position], true)
        }
        mViewModel.fileDetail.observeForever(observer)
    }

    private fun deleteTask(file: FileDetailBean?) {
        var savePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + file?.name
        File(savePath).delete()
        File(FileDownloadUtils.getTempPath(savePath)).delete()
    }

    fun getDownloadInfo(path: String, pos: Int) {
        mViewModel.loadFileDetail(path, pos)
    }

    fun startDownload(file: Direcotory, shouldDeleteOld: Boolean = false) {
        var savePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + file.name
        DLog.i(savePath + "-----------------------------")
        if (shouldDeleteOld) {
            File(savePath).delete()
            File(FileDownloadUtils.getTempPath(savePath)).delete()
        }
        val task = FileDownloader.getImpl().create(file.fileDetail?.downloadAddress)
                .setPath(savePath)
                .setTag(file)
                .setCallbackProgressTimes(100)
                .setListener(mTaskDownloadListener)
        TasksManager.getImpl().addTaskForViewHolder(task)
        task.start()
    }


    override fun convert(helper: BaseViewHolder?, item: Direcotory) {
        with(helper?.getView<ImageView>(R.id.mIcon)) {
            when {
                item?.mime!!.contains(Constants.MIME_FOLDER) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_directory))
                item.mime.contains(Constants.MIME_IMG) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_image))
                item.mime.contains(Constants.MIME_AUDIO) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_mp3))
                item.mime.contains(Constants.MIME_TEXT) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_txt))
                item.mime.contains(Constants.MIME_VIDEO) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_video))
                item.mime.contains(Constants.MIME_DOC) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_doc))
                item.mime.contains(Constants.MIME_EXCEL) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_excel))
                item.mime.contains(Constants.MIME_PDF) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_pdf))
                else -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_file_unkonw))
            }
        }
        helper?.setText(R.id.mTvFileName, item.name)

        with(helper?.getView<ImageView>(R.id.mIvStatus)) {
            when {
                item.downloadStatus == 0 -> {
                    this?.setImageDrawable(mContext.resources.getDrawable(R.drawable.icon_download_24dp))
                }
                item.downloadStatus == 1 -> {
                    this?.setImageDrawable(mContext.resources.getDrawable(R.drawable.icon_pause_24dp))
                }
                item.downloadStatus == 2 -> {
                    this?.setImageDrawable(mContext.resources.getDrawable(R.drawable.icon_download_24dp))
                }
                item.downloadStatus == 3 -> {
                    this?.setImageDrawable(mContext.resources.getDrawable(R.drawable.icon_download_24dp))
                }
                else -> {
                }
            }
        }

        with(helper?.getView<RingProgressBar>(R.id.mDlProgress)) {
            this?.progress = item?.downProgress
        }

        with(helper?.getView<FrameLayout>(R.id.mFlDownload)) {
            when {
                item.downloadStatus == 0 -> {
                    this?.visibility = View.VISIBLE
                }
                item.downloadStatus == 1 -> {
                    this?.visibility = View.VISIBLE
                }
                item.downloadStatus == 2 -> {
                    this?.visibility = View.VISIBLE
                }
                item.downloadStatus == 3 -> {
                    this?.visibility = View.VISIBLE
                }
                item.downloadStatus == 4 -> {
                    this?.visibility = View.GONE
                }
            }
        }

        with(helper?.getView<TextView>(R.id.mTvDownloadStatus)) {
            when {
                item.downloadStatus == 0 -> {
                    this?.text = "等待下载....."
                }
                item.downloadStatus == 1 -> {
                    this?.text = "正在下载 ${item?.downProgress}%"
                }
                item.downloadStatus == 2 -> {
                    this?.text = "下载暂停"
                }
                item.downloadStatus == 3 -> {
                    this?.text = "下载失败"
                }
                item.downloadStatus == 4 -> {
                    this?.text = "下载完成"
                }
            }
        }

        helper?.getView<FrameLayout>(R.id.mFlDownload)?.setOnClickListener {
            when (item.downloadStatus) {
                0, 3 -> {
                    getDownloadInfo(item.path, helper.adapterPosition)
                }
                2 -> {
                    item.fileDetail?.position = helper.adapterPosition
                    startDownload(item)
                }
                1 -> {
                    FileDownloader.getImpl().pause(item.taskId)
                }
            }
        }

        helper?.getView<Button>(R.id.btnDelete)?.setOnClickListener {
            deleteTask(item?.fileDetail)
            helper?.getView<SwipeMenuLayout>(R.id.mSwipeLayout)?.smoothClose()
            doAsync {
                val dao = App.getCloudDatabase()?.DirecotoryDao()
                dao?.deleteDirecotory(item)
            }
            mData.remove(item)
            notifyItemRemoved(helper.adapterPosition)
        }

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mViewModel.fileDetail.removeObserver(observer)
    }


}