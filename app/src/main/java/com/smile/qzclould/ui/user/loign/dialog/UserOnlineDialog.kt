package com.smile.qzclould.ui.user.loign.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.user.loign.adapter.UserOnlineAdapter
import com.smile.qzclould.ui.user.loign.viewmodel.UserViewModel
import com.smile.qzclould.utils.ViewUtils
import kotlinx.android.synthetic.main.dialog_user_online.*

/**
 * Created by wangzhg on 2019/6/30
 * Describe:
 */
class UserOnlineDialog: BaseDialogFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(UserViewModel::class.java) }
    private val mAdapter by lazy { UserOnlineAdapter() }

    override fun setLayoutId(): Int {
        return R.layout.dialog_user_online
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.CENTER)
        mWindow.setWindowAnimations(R.style.MyBottomDialog)
        mWindow.setLayout(ViewUtils.dip2px(270f).toInt(), ViewUtils.dip2px(354f).toInt())
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(mActivity)
        rvList.layoutManager = layoutManager
        mAdapter.bindToRecyclerView(rvList)
        mModel.getOnlineInfo()

        tvCancel.setOnClickListener {
            dismiss()
        }

        tvConfirm.setOnClickListener {
            if(mAdapter.deviceList.isNullOrEmpty()) {
                showToast(Constants.TOAST_ERROR, "请选择至少设备")
                return@setOnClickListener
            }
            mModel.logoutOther(mAdapter.deviceList)
        }
    }

    override fun initData() {
        mModel.onlineInfoRsp.observe(this, Observer {
            mAdapter.setNewData(it?.online)
        })
        mModel.logoutOtherRsp.observe(this, Observer {
            dismiss()
            if(it!!.success) {
                showToast(Constants.TOAST_SUCCESS, "退出成功")
            } else {
                showToast(Constants.TOAST_ERROR, "退出失败")
            }
        })
    }

}