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
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.event.FileDownloadCompleteEvent
import com.smile.qzclould.ui.transfer.bean.FileDetailBean
import com.smile.qzclould.ui.transfer.viewmodel.TransferViewModel
import com.smile.qzclould.utils.RxBus
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import org.jetbrains.anko.doAsync
import java.io.File

class LocalDownloadAdapter : BaseQuickAdapter<Direcotory, BaseViewHolder> {
    private val mViewModel by lazy { TransferViewModel() }
    lateinit var observer: Observer<FileDetailBean>
    private val waitDownLoadList = ArrayList<Direcotory>()

    companion object {
        var savePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator
    }

    private val mTaskDownloadListener = object : FileDownloadSampleListener() {
        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            super.pending(task, soFarBytes, totalBytes)
            val itemData = task?.tag as Direcotory
            itemData?.downloadStatus = 5
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
            notifyDataSetChanged()
//            notifyItemRemoved(index)
//            notifyItemRangeChanged(index, mData.size)
            deleteFile(itemData)
            RxBus.post(FileDownloadCompleteEvent())
        }
    }

    constructor() : super(R.layout.item_local_download) {
        initViewModel()
    }

    fun startDownload(file: Direcotory) {
        val path = savePath + file.name

        val task = FileDownloader.getImpl().create(file.fileDetail?.downloadAddress)
                .setPath(path)
                .setTag(file)
                .setCallbackProgressTimes(750)
                .setListener(mTaskDownloadListener)
        task.start()
    }

    override fun setNewData(data: List<Direcotory>?) {
        super.setNewData(data)

        for (item in data!!) {
            when (item.downloadStatus) {
                1, 5 -> {
                    if (item.fileDetail != null) {
                        startDownload(item!!)
                    } else {
                        getDownloadInfo(item.path, data.indexOf(item))
                    }
                }
                0 -> {
                    getDownloadInfo(item.path, data.indexOf(item))
                }
            }
        }
    }

    private fun deleteFile(fileInfo: Direcotory?) {
        if (fileInfo == null) {
            return
        }
        doAsync {
            val dao = App.getCloudDatabase()?.DirecotoryDao()
            dao?.deleteDirecotory(fileInfo)
        }
    }

    private fun updateFileInfo(fileInfo: Direcotory?) {
        if (fileInfo == null) {
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
            startDownload(data[it!!.position])
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
                item.mime.contains(Constants.MIME_ZIP) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_zip))
                else -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_file_unkonw))
            }
        }
        helper?.setText(R.id.mTvFileName, item.name)

        with(helper?.getView<ImageView>(R.id.mIvStatus)) {
            this?.setImageDrawable(mContext.resources.getDrawable(when (item.downloadStatus) {
                0 -> R.drawable.icon_download_24dp
                1 -> R.drawable.icon_pause_24dp
                2 -> R.drawable.icon_download_24dp
                3 -> R.drawable.icon_download_24dp
                5 -> R.drawable.ic_queue_24dp
                else -> 0
            }))
        }

        with(helper?.getView<RingProgressBar>(R.id.mDlProgress)) {
            this?.progress = item?.downProgress
        }

        with(helper?.getView<FrameLayout>(R.id.mFlDownload)) {
            when (item.downloadStatus) {
                0, 1, 2, 3, 5 -> {
                    this?.visibility = View.VISIBLE
                }
                4 -> {
                    this?.visibility = View.GONE
                }
            }
        }

        with(helper?.getView<TextView>(R.id.mTvDownloadStatus)) {
            this?.text = when (item.downloadStatus) {
                0 -> "等待下载....."
                1 -> "正在下载 ${item?.downProgress}%"
                2 -> "下载暂停"
                3 -> "下载失败"
                4 -> "下载完成"
                5 -> "队列中"
                else -> ""
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
                1, 5 -> {
                    FileDownloader.getImpl().pause(item.taskId)
                }
            }
        }


    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mViewModel.fileDetail.removeObserver(observer)
    }


}