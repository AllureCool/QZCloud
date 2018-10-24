package com.smile.qzclould.ui.cloud.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.event.*
import com.smile.qzclould.ui.cloud.adapter.FileListAdapter
import com.smile.qzclould.ui.cloud.dialog.BuildNewFolderDialog
import com.smile.qzclould.ui.cloud.dialog.ConfirmPlayDialog
import com.smile.qzclould.ui.cloud.dialog.FileOperationDialog
import com.smile.qzclould.ui.cloud.viewmodel.CloudViewModel
import com.smile.qzclould.ui.component.FileDeleteDialog
import com.smile.qzclould.ui.preview.picture.PicturePreviewActivity
import com.smile.qzclould.ui.player.PdfViewActivity
import com.smile.qzclould.ui.preview.player.activity.AudioPlayerActivity
import com.smile.qzclould.ui.preview.player.activity.PlayerActivity
import com.smile.qzclould.utils.DLog
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_home_first.*
import kotlinx.android.synthetic.main.view_search_bar.*

class HomeFirstFragment : BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(CloudViewModel::class.java) }
    private var mFileOperationDialog: FileOperationDialog? = null
    private val mDialog by lazy { BuildNewFolderDialog() }
    private val mPlayConfirmDialog by lazy { ConfirmPlayDialog() }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mAdapter by lazy { FileListAdapter(mModel, this@HomeFirstFragment.hashCode()) }
    private val mPageSize = 20

    private var mPage = 1

    private var mOrderType = 0 //排序 0按 文件名 1 按时间

    private var mFilePath = "/"
    private var mFileName = ""

    override fun getLayoutId(): Int {
        return R.layout.frag_home_first
    }

    override fun initData() {
        if (arguments != null && arguments!!.getString("file_path") != null) {
            mFilePath = arguments!!.getString("file_path")
            mFileName = arguments!!.getString("file_name")
            Constants.pathList.add(mFileName)
        }
        listFileByPath()
    }

    override fun initView(savedInstanceState: Bundle?) {

        if (!TextUtils.isEmpty(mFileName)) {
            mTvFileName.visibility = View.VISIBLE
            mTvFileName.text = mFileName
        } else {
            mTvFileName.visibility = View.GONE
        }

        mTvFileName.setOnClickListener {
            if (Navigation.findNavController(it).navigateUp()) {
                Constants.pathList.remove(mFileName)
            } else {
                mActivity?.finish()
            }
        }

        mRvFile.layoutManager = mLayoutManager
        mAdapter.bindToRecyclerView(mRvFile)

        mRefreshLayout.setColorSchemeColors(resources.getColor(R.color.color_green_2EC17C))
    }

    override fun initListener() {
        mNewFloder.setOnClickListener {
            mDialog.setDialogClickListener(object : BuildNewFolderDialog.DialogButtonClickListener {
                override fun onConfirmClick(folderName: String) {
                    showLoading()
                    mModel.createDirectory(folderName, mFilePath)
                }
            })
            if (!mDialog.isAdded) {
                mDialog.show(childFragmentManager, "new_folder")
            }
        }

        mAdapter.setOnLoadMoreListener({ listFileByPath() }, mRvFile)

        mAdapter.setOnItemLongClickListener { adapter, view, position ->

            if (mFileOperationDialog == null) {
                mFileOperationDialog = FileOperationDialog()
            }

            if (!mFileOperationDialog!!.isAdded) {
                val bundle = Bundle()
                bundle.putInt("eventId", this@HomeFirstFragment.hashCode())
                mFileOperationDialog?.arguments = bundle
                mFileOperationDialog?.show(childFragmentManager, "file_operation")
            }

            return@setOnItemLongClickListener true
        }

        mAdapter.setOnCheckListener(object : FileListAdapter.OnCheckListener {
            override fun onChecked(position: Int, item: Direcotory?) {
                showFileOperationDialog(true)
            }

            override fun onItemClick(position: Int, item: Direcotory?) {
                DLog.i(this@HomeFirstFragment.hashCode().toString() + "********************")
                if (mFileOperationDialog != null && mFileOperationDialog!!.isAdded) {
                    RxBus.post(FileControlEvent(EVENT_CANCEl, this@HomeFirstFragment.hashCode()))
                    mFileOperationDialog!!.dismissDialog()
                    return
                }
                when {
                    item?.mime == Constants.MIME_FOLDER -> {
                        val bundle = Bundle()
                        bundle.putString("file_name", item.name)
                        bundle.putString("file_path", item.path)
                        Navigation.findNavController(mRvFile).navigate(R.id.homeFirstFragment2, bundle)
                    }
                    item?.mime!!.contains(Constants.MIME_VIDEO) -> {
                        val bundle = Bundle()
                        bundle.putBoolean("isLocal", false)
                        bundle.putString("path", item.path)
                        bundle.putBoolean("hasPreview", item.hasPreview)
                        if(item.hasPreview) {
                            jumpActivity(PlayerActivity::class.java, bundle)
                        } else {
                            mPlayConfirmDialog.show(childFragmentManager, this@HomeFirstFragment.hashCode().toString())
                            mPlayConfirmDialog.setDialogClickListener(object: ConfirmPlayDialog.DialogButtonClickListener {
                                override fun onConfirmClick() {
                                    jumpActivity(PlayerActivity::class.java, bundle)
                                }
                            })
                        }
                    }
                    item?.mime!!.contains(Constants.MIME_AUDIO) -> {
                        val bundle = Bundle()
                        bundle.putBoolean("isLocal", false)
                        bundle.putString("path", item.path)
                        bundle.putString("audio_name", item.name)
                        jumpActivity(AudioPlayerActivity::class.java, bundle)
                    }

                    item?.mime!!.contains(Constants.MIME_IMG) -> {
                        val bundle = Bundle()
                        bundle.putBoolean("isLocal", false)
                        bundle.putString("path", item.path)
                        jumpActivity(PicturePreviewActivity::class.java, bundle)
                    }
                    item?.mime!!.contains(Constants.MIME_DOC) || item?.mime!!.contains(Constants.MIME_PDF) || item?.mime!!.contains(Constants.MIME_EXCEL) || item?.mime!!.contains(Constants.MIME_TEXT) -> {
                        val bundle = Bundle()
                        bundle.putBoolean("isLocal", false)
                        bundle.putString("path", item.path)
                        bundle.putString("name", item.name)
                        jumpActivity(PdfViewActivity::class.java, bundle)
                    }
                }
            }

            override fun onItemLongClick(position: Int, item: Direcotory?) {
                showFileOperationDialog(false)
            }

        })

        mRefreshLayout.setOnRefreshListener {
            mPage = 1
            listFileByPath()
        }

        mBtnSort.setOnClickListener {
            mPage = 1
            when {
                mOrderType == 0 -> mOrderType = 1
                mOrderType == 1 -> mOrderType = 0
            }
            listFileByPath()
        }
    }

    override fun initViewModel() {

        mModel.listFileResult.observe(this, Observer {

            mRefreshLayout.isRefreshing = false
            if (mPage == 1 && it!!.isEmpty()) {
                mAdapter.setEmptyView(R.layout.view_empty)
            } else {
                if (it!!.isEmpty()) {
                    mAdapter.loadMoreEnd(true)
                } else {
                    mAdapter.loadMoreComplete()
                }
                if (mPage == 1) {
                    mAdapter.setNewData(it)
                } else {
                    mAdapter.addData(it)
                }
                mPage++
            }
        })

        mModel.createDirectoryResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, App.instance.getString(R.string.create_directory_success))
            mAdapter.addData(0, it!!)
        })

        mModel.parseUrlResult.observe(this, Observer {
            mModel.offlineDownloadStart(it!!.taskHash, "", arrayOf())
        })

        mModel.errorStatus.observe(this, Observer {
            mRefreshLayout.isRefreshing = false
            stopLoading()
            showToast(Constants.TOAST_NORMAL, it?.errorMessage!!)
        })
    }

    override fun initEvent() {
        RxBus.toObservable(ClickThroughEvent::class.java)
                .subscribe {
                    mRvFile?.dispatchTouchEvent(it.event)
                }
                .autoDispose()
        RxBus.toObservable(BackPressEvent::class.java)
                .subscribe{
                    if(isVisible) {
                        DLog.i("visible---------------------------------")
                        if(Navigation.findNavController(mRvFile).navigateUp()) {
                            Constants.pathList.remove(mFileName)
                        }
                    }
                }
                .autoDispose()
    }

    private fun listFileByPath() {
        mModel.listFileByPath(mFilePath, mPage, mPageSize, mOrderType)
    }

    private fun showFileOperationDialog(showDownloadBtn: Boolean) {
        if (mFileOperationDialog == null) {
            mFileOperationDialog = FileOperationDialog()
        }

        if (!mFileOperationDialog!!.isAdded) {
            val bundle = Bundle()
            bundle.putBoolean("show_download_btn", showDownloadBtn)
            bundle.putInt("eventId", this@HomeFirstFragment.hashCode())
            mFileOperationDialog!!.arguments = bundle

            val ft = childFragmentManager.beginTransaction()
            ft.add(mFileOperationDialog, mFileOperationDialog?.javaClass?.simpleName)
            ft.commitAllowingStateLoss()
        }
    }
}