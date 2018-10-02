package com.smile.qzclould.ui.cloud.adapter

import android.arch.lifecycle.Observer
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.event.*
import com.smile.qzclould.ui.cloud.viewmodel.CloudViewModel
import com.smile.qzclould.utils.DateUtils
import com.smile.qzclould.utils.RxBus
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FileListAdapter : BaseQuickAdapter<Direcotory, BaseViewHolder> {

    private var mCheckListener: OnCheckListener? = null
    private var mDispose: Disposable? = null
    private val mSelectedList by lazy { mutableListOf<Direcotory>() }
    private var mViewModel: CloudViewModel? = null
    lateinit var observer: Observer<String>

    constructor(viewModel: CloudViewModel) : super(R.layout.item_file) {
        mViewModel = viewModel
        initViewModel()
        initEvent()
    }

    fun setOnCheckListener(listener: OnCheckListener) {
        mCheckListener = listener
    }

    override fun convert(helper: BaseViewHolder?, item: Direcotory?) {
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
                mSelectedList.add(item)
                item.isSelected = true
                notifyItemChanged(helper!!.adapterPosition)
                mCheckListener?.onChecked(helper.adapterPosition, item)
            }
        }

        helper?.getView<Button>(R.id.btnDelete)?.setOnClickListener {
            mSelectedList.clear()
            mSelectedList.add(item!!)
            removeFiles()
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
        }
        mViewModel?.removeResult?.observeForever(observer)
    }

    private fun initEvent() {
        mDispose = RxBus.toObservable(FileControlEvent::class.java)
                .subscribe {
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
                            doAsync {
                                val dao = App.getCloudDatabase()?.DirecotoryDao()
                                dao?.saveDirecotoryList(downloadList)
                                RxBus.post(FileDownloadEvent(true))
                            }
                        }
                        EVENT_DELETE -> {
                            removeFiles()
                        }
                    }
                }
    }

    private fun removeFiles() {
        doAsync {
            val removeList = mutableListOf<String>()
            for (file in mSelectedList) {
                removeList.add(file.path)
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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (!mDispose?.isDisposed!!) {
            mDispose?.dispose()
        }
    }

    interface OnCheckListener {
        fun onChecked(position: Int, item: Direcotory?)

        fun onItemClick(position: Int, item: Direcotory?)

        fun onItemLongClick(position: Int, item: Direcotory?)
    }
}