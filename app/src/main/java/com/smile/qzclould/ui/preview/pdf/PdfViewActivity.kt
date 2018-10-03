package com.smile.qzclould.ui.player

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.text.TextUtils
import com.gyf.barlibrary.ImmersionBar
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.ui.preview.pdf.PdfDetailBean
import com.smile.qzclould.ui.preview.pdf.PdfViewModel
import com.smile.qzclould.ui.transfer.adapter.LocalDownloadAdapter
import kotlinx.android.synthetic.main.pdf_view_activity.*
import java.io.File

class PdfViewActivity : BaseActivity() {
    private val model by lazy { ViewModelProviders.of(this).get(PdfViewModel::class.java) }
    private lateinit var mPath: String
    private var mIsLocal: Boolean = false
    private lateinit var name: String

    override fun setLayoutId(): Int {
        return R.layout.pdf_view_activity
    }

    override fun initData() {
        super.initData()
        mIsLocal = intent.getBundleExtra("bundle_extra").getBoolean("isLocal")
        mPath = intent.getBundleExtra("bundle_extra").getString("path")
        name = intent.getBundleExtra("bundle_extra").getString("name")
        if (mIsLocal) {
            showPdf(mPath)
        } else {
            model.mediaInfoResult.observe(this, Observer<PdfDetailBean> {
                if (!TextUtils.isEmpty(it?.url)) {
                    FileDownloadUtils.getDefaultSaveRootPath() + File.separator
                    val file = LocalDownloadAdapter.savePath + name + ".pdf"
                    val task = FileDownloader.getImpl().create(it?.url!!)
                            .setPath(file)
                            .setTag(it?.url)
                            .setCallbackProgressTimes(0)
                            .setListener(object : FileDownloadListener() {
                                override fun warn(task: BaseDownloadTask?) {
                                }

                                override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                                }

                                override fun error(task: BaseDownloadTask?, e: Throwable?) {

                                }

                                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                                }

                                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                                }

                                override fun completed(task: BaseDownloadTask?) {
                                    showPdf(file)
                                }

                            })
                    task.start()
                }
            })
            model.getPdfInfo(mPath)
        }
    }

    private fun showPdf(path: String) {
        pdf_view.fromFile(File(path))
                .load()
    }

    override fun initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar?.statusBarDarkFont(true, 0.2f)
        mImmersionBar?.statusBarColor(R.color.color_white_ffffff)
        mImmersionBar?.fitsSystemWindows(false)
        mImmersionBar?.init()
    }

}
