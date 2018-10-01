package com.smile.qzclould.ui.cloud.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.ui.cloud.adapter.FileListAdapter
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.ui.cloud.dialog.BuildNewFolderDialog
import com.smile.qzclould.ui.cloud.viewmodel.CloudViewModel
import kotlinx.android.synthetic.main.frag_folder_detail.*
import kotlinx.android.synthetic.main.view_search_bar.*

class FolderDetailFragment : BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(CloudViewModel::class.java) }
    private val mDialog by lazy { BuildNewFolderDialog() }
    private val mLayoutManager by lazy { LinearLayoutManager(mActivity) }
    private val mAdapter by lazy { FileListAdapter(mModel) }
    private val mPageSize = 20

    private var mOffset = 0
    private lateinit var mParentUuid: String
    private lateinit var mParentName: String

    override fun getLayoutId(): Int {
        return R.layout.frag_folder_detail
    }

    override fun initData() {
        mParentUuid = mActivity?.intent!!.getBundleExtra("bundle_extra").getString("parent_uuid")
        mParentName = mActivity?.intent!!.getBundleExtra("bundle_extra").getString("parent_name")
        if (arguments?.getString("parent_uuid") != null) {
            mParentUuid = arguments!!.getString("parent_uuid")
            mParentName = arguments!!.getString("parent_name")
        }
        loadFileList()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBtnBack.text = mParentName
        mRvFile.layoutManager = mLayoutManager
        mAdapter.bindToRecyclerView(mRvFile)
    }

    override fun initListener() {

        mBtnBack.setOnClickListener {
            if(!Navigation.findNavController(it).navigateUp()) {
                mActivity?.finish()
            }
        }

        mNewFloder.setOnClickListener {
            mDialog.setDialogClickListener(object : BuildNewFolderDialog.DialogButtonClickListener {
                override fun onConfirmClick(folderName: String) {
                    showLoading()
                    mModel.createDirectory(folderName, mParentUuid)
                }
            })
            if (!mDialog.isAdded) {
                mDialog.show(childFragmentManager, "new_folder")
            }
        }

        mAdapter.setOnLoadMoreListener({ loadFileList() }, mRvFile)

        mAdapter.setOnItemClickListener { adapter, view, position ->
            if ((adapter.getItem(position) as Direcotory).mime == "application/x-directory") {
                val bundle = Bundle()
                bundle.putString("parent_name", (adapter.getItem(position) as Direcotory).name)
                bundle.putString("parent_uuid", (adapter.getItem(position) as Direcotory).uuid)
                Navigation.findNavController(mRvFile).navigate(R.id.folderDetailFragment, bundle)
            }
        }

        mRefreshLayout.setOnRefreshListener {
            mOffset = 0
            loadFileList()
        }
    }

    override fun initViewModel() {

        mModel.listFileResult.observe(this, Observer {

            mRefreshLayout.isRefreshing = false
            if(mOffset == 0 && it!!.isEmpty()) {
                mAdapter.setEmptyView(R.layout.view_empty)
            } else {
                if (it!!.isEmpty()) {
                    mAdapter.loadMoreEnd(true)
                } else {
                    mAdapter.loadMoreComplete()
                }
                if (mOffset == 0) {
                    mAdapter.setNewData(it)
                } else {
                    mAdapter.addData(it)
                }
                mOffset += it?.size!!
            }
        })

        mModel.createDirectoryResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, App.instance.getString(R.string.create_directory_success))
            mAdapter.addData(0, it!!)
            mOffset += 1
        })

        mModel.errorStatus.observe(this, Observer {
            mRefreshLayout.isRefreshing = false
            stopLoading()
            showToast(Constants.TOAST_ERROR, it?.errorMessage!!)
        })
    }

    private fun loadFileList() {
        mModel.listFile(mParentUuid, "", mOffset, mPageSize, 0, "", 0, 1)
    }
}