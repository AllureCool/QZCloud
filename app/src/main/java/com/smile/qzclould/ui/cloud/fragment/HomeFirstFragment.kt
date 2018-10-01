package com.smile.qzclould.ui.cloud.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.ui.cloud.activity.FolderDetailActivity
import com.smile.qzclould.ui.cloud.adapter.FileListAdapter
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.ui.cloud.dialog.BuildNewFolderDialog
import com.smile.qzclould.ui.cloud.dialog.FileOperationDialog
import com.smile.qzclould.ui.cloud.viewmodel.CloudViewModel
import com.smile.qzclould.utils.DLog
import kotlinx.android.synthetic.main.frag_home_first.*
import kotlinx.android.synthetic.main.view_search_bar.*
import org.jetbrains.anko.doAsync

class HomeFirstFragment: BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(CloudViewModel::class.java) }
    private var mFileOperationDialog: FileOperationDialog? = null
    private val mDialog by lazy { BuildNewFolderDialog() }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mAdapter by lazy { FileListAdapter(mModel) }
    private val mPageSize = 20

    private var mPage = 1

    private var mOrderType = 0 //排序 0按 文件名 1 按时间

    override fun getLayoutId(): Int {
        return R.layout.frag_home_first
    }

    override fun initData() {
        listFileByPath()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mRvFile.layoutManager = mLayoutManager
        mAdapter.bindToRecyclerView(mRvFile)
        doAsync {
            val dao = App.getCloudDatabase()?.DirecotoryDao()
            DLog.i(dao!!.loadDirecotory().size.toString() + "================")
        }
    }

    override fun initListener() {
        mNewFloder.setOnClickListener {
            mDialog.setDialogClickListener(object : BuildNewFolderDialog.DialogButtonClickListener {
                override fun onConfirmClick(folderName: String) {
                    showLoading()
                    mModel.createDirectory(folderName)
                }
            })
            if(!mDialog.isAdded) {
                mDialog.show(childFragmentManager, "new_folder")
            }
        }

        mAdapter.setOnLoadMoreListener( { listFileByPath() }, mRvFile)

        mAdapter.setOnItemClickListener { adapter, view, position ->
            if((adapter.getItem(position) as Direcotory).mime == Constants.MIME_FOLDER) {
                val bundle = Bundle()
                bundle.putString("parent_name", (adapter.getItem(position) as Direcotory).name)
                bundle.putString("parent_uuid", (adapter.getItem(position) as Direcotory).uuid)
                jumpActivity(FolderDetailActivity::class.java, bundle)
            }
        }

        mAdapter.setOnCheckListener(object : FileListAdapter.OnCheckListener {
            override fun onChecked(position: Int, item: Direcotory?) {
//                doAsync {
//                    val dao = App.getCloudDatabase()?.DirecotoryDao()
//                    dao?.saveDirecotory(item!!)
//
//                    DLog.i(dao!!.loadDirecotory()[0].uuid + "--------------------")
//                }
                mFileOperationDialog = FileOperationDialog()
                val bundle = Bundle()
                bundle.putSerializable("file_info", item)
                mFileOperationDialog?.arguments = bundle
                mFileOperationDialog?.show(childFragmentManager, "file_operation")
            }

            override fun onItemClick(position: Int, item: Direcotory?) {
                if(item?.mime == Constants.MIME_FOLDER) {
                    val bundle = Bundle()
                    bundle.putString("parent_name", item.name)
                    bundle.putString("parent_uuid", item.uuid)
                    jumpActivity(FolderDetailActivity::class.java, bundle)
                }
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
            if(mPage == 1 && it!!.isEmpty()) {
                mAdapter.setEmptyView(R.layout.view_empty)
            } else {
                if(it!!.isEmpty()) {
                    mAdapter.loadMoreEnd(true)
                } else {
                    mAdapter.loadMoreComplete()
                }
                if(mPage == 1) {
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
            showToast(Constants.TOAST_ERROR, it?.errorMessage!!)
        })
    }

    private fun listFileByPath() {
        mModel.listFileByPath("/", mPage, mPageSize, mOrderType)
    }

}