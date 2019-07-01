package com.smile.qzclould.ui.cloud.adapter

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.event.*
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.repository.requestbody.MoveFileBodyV2
import com.smile.qzclould.repository.requestbody.PathArrayBodyV2
import com.smile.qzclould.ui.MainActivity
import com.smile.qzclould.ui.cloud.viewmodel.CloudViewModel
import com.smile.qzclould.ui.user.loign.activity.LoginActivity
import com.smile.qzclould.utils.DLog
import com.smile.qzclould.utils.DateUtils
import com.smile.qzclould.utils.RxBus
import es.dmoral.toasty.Toasty
import hei.permission.PermissionActivity
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FileListAdapter : BaseQuickAdapter<Direcotory, BaseViewHolder> {

    private var mCheckListener: OnCheckListener? = null
    private var mDispose: Disposable? = null
    private var mDispose1: Disposable? = null
    private val mSelectedList by lazy { mutableListOf<Direcotory>() }
    private var mViewModel: CloudViewModel? = null
    lateinit var observer: Observer<String>

    constructor(viewModel: CloudViewModel, eventId: Int) : super(R.layout.item_file) {
        mViewModel = viewModel
        initViewModel()
        initEvent(eventId)
    }

    fun setOnCheckListener(listener: OnCheckListener) {
        mCheckListener = listener
    }

    override fun convert(helper: BaseViewHolder?, item: Direcotory?) {

        with(helper?.getView<ImageView>(R.id.mIcon)) {
            if (item?.mime != null) {
                when {
                    item.mime.contains(Constants.MIME_FOLDER) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_directory))
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
            } else {
                this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_file_unkonw))
            }
        }
        helper?.setText(R.id.mTvFileName, item?.name)
        helper?.setText(R.id.mTvDate, DateUtils.dateFormat(item?.ctime!!))

        with(helper?.getView<ImageView>(R.id.mIvSelect)) {
            this?.isSelected = item!!.isSelected
            if (item.mime == Constants.MIME_FOLDER) {
                this?.visibility = View.GONE
            } else {
                this?.visibility = View.VISIBLE
            }
            this?.setOnClickListener {
                if (item.isSelected) {
                    mSelectedList.remove(item)
                } else {
                    mSelectedList.add(item)
                }
                item.isSelected = !item.isSelected
                notifyItemChanged(helper!!.adapterPosition)
                mCheckListener?.onChecked(helper.adapterPosition, item)
            }
        }

        helper?.getView<ConstraintLayout>(R.id.mClItem)?.setOnClickListener {
            mCheckListener?.onItemClick(helper.adapterPosition, item)
        }

        helper?.getView<ConstraintLayout>(R.id.mClItem)?.setOnLongClickListener {
            mSelectedList.add(item!!)
            item.isSelected = true
            notifyItemChanged(helper.adapterPosition)
            mCheckListener?.onItemLongClick(helper.adapterPosition, item)
            return@setOnLongClickListener true
        }
    }

    private fun initViewModel() {
        observer = Observer {
            Toasty.success(App.instance, mContext.getString(R.string.deleting)).show()
            data.removeAll(mSelectedList)
            notifyDataSetChanged()
            mSelectedList.clear()
        }
        mViewModel?.removeResult?.observeForever(observer)

        val moveObserver = Observer<String> {
            Toasty.success(App.instance, mContext.getString(R.string.move_success)).show()
            data.removeAll(mSelectedList)
            notifyDataSetChanged()
            mSelectedList.clear()
        }
        mViewModel?.moveFileResult?.observeForever(moveObserver)

        val copyObserver = Observer<String> {
            mSelectedList.clear()
            Toasty.success(App.instance, mContext.getString(R.string.copy_success)).show()
        }
        mViewModel?.copyFileResult?.observeForever(copyObserver)
    }

    private fun initEvent(eventId: Int) {
        mDispose = RxBus.toObservable(FileControlEvent::class.java)
                .subscribe {
                    if (it.eventId == eventId) {
                        when (it.controlCode) {
                            EVENT_CANCEl -> {
                                mSelectedList.clear()
                                for (item in data) {
                                    item.isSelected = false
                                }
                                notifyDataSetChanged()
                            }
                            EVENT_SELECTALL -> {
                                mSelectedList.clear()
                                for (item in data) {
                                    item.isSelected = true
                                    mSelectedList.add(item)
                                }
                                notifyDataSetChanged()
                            }
                            EVENT_DOWNLOAD -> {
                                checkPermission()
                            }
                            EVENT_DELETE -> {
                                removeFiles()
                            }
                        }
                    }
                }
        mDispose1 = RxBus.toObservable(SelectDownloadPathEvent::class.java)
                .subscribe {
                    if (it.eventId == eventId) {
                        when (it.opt) {
                            1 -> moveFiles(it.path)
                            2 -> copyFiles(it.path)
                        }
                    }
                }
    }

    private fun checkPermission() {
        (mContext as PermissionActivity).checkPermission(PermissionActivity.CheckPermListener {
            val downloadList = mutableListOf<Direcotory>()
            for (file in mSelectedList) {
                if (file.mime != Constants.MIME_FOLDER) {
                    downloadList.add(file)
                }
            }
            mSelectedList.clear()
            for (item in data) {
                item.isSelected = false
            }
            notifyDataSetChanged()
            if(downloadList.isEmpty()) {
                Toasty.normal(mContext, mContext.getString(R.string.please_select_file)).show()
            } else {
                Toasty.success(mContext, mContext.getString(R.string.downloading)).show()
                doAsync {
                    try {
                        val dao = App.getCloudDatabase()?.DirecotoryDao()
                        val flag = dao?.saveDirecotoryList(downloadList)
                        DLog.i(flag.toString() + "&&&&&&&&&&&&&&&")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    uiThread {
                        RxBus.post(FileDownloadEvent(true))
                    }
                }
            }
        }, R.string.need_storage_permission,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun removeFiles() {
        doAsync {
            val removeList = mutableListOf<PathArrayBodyV2.Source>()
            for (file in mSelectedList) {
                val source = PathArrayBodyV2.Source(file.path)
                removeList.add(source)
            }
            uiThread {
                for (item in data) {
                    item.isSelected = false
                }
                notifyDataSetChanged()
                mViewModel?.removeFile(removeList)
            }
        }
    }

    private fun moveFiles(destPath: String) {
        doAsync {
            val moveList = mutableListOf<MoveFileBodyV2.Source>()
            for (file in mSelectedList) {
                val source = MoveFileBodyV2.Source(file.path)
                moveList.add(source)
            }
            uiThread {
                for (item in data) {
                    item.isSelected = false
                }
                notifyDataSetChanged()
                val destination = MoveFileBodyV2.Destination(destPath)
                mViewModel?.moveFile(moveList, destination)
            }
        }
    }

    private fun copyFiles(destPath: String) {
        doAsync {
            val copyList = mutableListOf<MoveFileBodyV2.Source>()
            for (file in mSelectedList) {
                val source = MoveFileBodyV2.Source(file.path)
                copyList.add(source)
            }
            uiThread {
                for (item in data) {
                    item.isSelected = false
                }
                notifyDataSetChanged()
                val destination = MoveFileBodyV2.Destination(destPath)
                mViewModel?.copyFile(copyList, destination)
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (!mDispose?.isDisposed!!) {
            mDispose?.dispose()
        }
        if (!mDispose1?.isDisposed!!) {
            mDispose1?.dispose()
        }
    }

    interface OnCheckListener {
        fun onChecked(position: Int, item: Direcotory?)

        fun onItemClick(position: Int, item: Direcotory?)

        fun onItemLongClick(position: Int, item: Direcotory?)
    }
}