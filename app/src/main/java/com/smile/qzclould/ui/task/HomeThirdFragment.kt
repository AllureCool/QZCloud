package com.smile.qzclould.ui.task

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.event.FileDownloadCompleteEvent
import com.smile.qzclould.ui.component.FileDeleteDialog
import com.smile.qzclould.ui.preview.picture.PicturePreviewActivity
import com.smile.qzclould.ui.player.PdfViewActivity
import com.smile.qzclould.ui.preview.player.activity.AudioPlayerActivity
import com.smile.qzclould.ui.preview.player.activity.PlayerActivity
import com.smile.qzclould.ui.task.adapter.FileDownloadCompleteAdapter
import com.smile.qzclould.utils.CallOtherOpenFile
import com.smile.qzclould.utils.FileUtils
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_home_third.*
import java.io.File

class HomeThirdFragment: BaseFragment() {

    private val path = FileDownloadUtils.getDefaultSaveRootPath() + File.separator
    private val mAdapter by lazy { FileDownloadCompleteAdapter() }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mFileDeleteDialog by lazy { FileDeleteDialog() }
    private var mDeleteFile: File ? = null

    private fun loadAlreadyDownloadFiles(): MutableList<File> {
        val file = File(path)
        val list = mutableListOf<File>()
        for (item in file.listFiles()) {
            list.add(item)
        }
        return list
    }

    private fun refreshList() {
        val fileList = loadAlreadyDownloadFiles()
        mAdapter.setNewData(fileList)
        if(fileList.isEmpty()) {
            mAdapter.setEmptyView(R.layout.view_empty)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_home_third
    }

    override fun initView(savedInstanceState: Bundle?) {
        mRvFile.layoutManager = mLayoutManager
        mAdapter.bindToRecyclerView(mRvFile)
        mAdapter.setOnFileRemoveListener(object : FileDownloadCompleteAdapter.OnFileRemoveListener {
            override fun onRemove(file: File) {
                mDeleteFile = file

                if(!mFileDeleteDialog.isAdded) {
                    mFileDeleteDialog.showNow(childFragmentManager, "file_delete_dialog")
                }

            }
        })

        mAdapter.setOnItemClickListener { adapter, view, position ->
            val file=adapter.getItem(position) as File
            val mimeType = FileUtils.getMIMEType(file)
            when {
                mimeType!!.contains(Constants.MIME_VIDEO) -> {
                    val bundle = Bundle()
                    bundle.putBoolean("isLocal", true)
                    bundle.putString("path", file.absolutePath)
                    jumpActivity(PlayerActivity::class.java, bundle)
                }
                mimeType!!.contains(Constants.MIME_AUDIO) -> {
                    val bundle = Bundle()
                    bundle.putBoolean("isLocal", true)
                    bundle.putString("path", file.absolutePath)
                    bundle.putString("audio_name", file.name)
                    jumpActivity(AudioPlayerActivity::class.java, bundle)
                }
                mimeType!!.contains(Constants.MIME_IMG) -> {
                    val bundle = Bundle()
                    bundle.putBoolean("isLocal", true)
                    bundle.putString("path", file.absolutePath)
                    jumpActivity(PicturePreviewActivity::class.java, bundle)
                }
                mimeType!!.contains(Constants.MIME_PDF) -> {
                    val bundle = Bundle()
                    bundle.putBoolean("isLocal", true)
                    bundle.putString("path", file.absolutePath)
                    bundle.putString("name", file.name)
                    jumpActivity(PdfViewActivity::class.java, bundle)
                }
                mimeType!!.contains(Constants.MIME_DOC) ||
                        mimeType!!.contains(Constants.MIME_TEXT) ||
                        mimeType!!.contains(Constants.MIME_EXCEL) -> {
                    startActivity(getPdfFileIntent(file))

                }
            }
        }

        mFileDeleteDialog?.setOnDialogClickListener(object : FileDeleteDialog.OnDialogClickListener {
            override fun onDeleteClick() {
                mDeleteFile!!.deleteRecursively()
                refreshList()
            }
        })
        refreshList()
    }

    fun getPdfFileIntent(file: File): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, FileUtils.getMIMEType(file))
        return Intent.createChooser(intent, "Open File")
    }
    override fun initEvent() {
        RxBus.toObservable(FileDownloadCompleteEvent::class.java)
                .subscribe {
                    refreshList()
                }
                .autoDispose()
    }

}