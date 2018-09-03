package com.smile.qzclould.ui.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.user.loign.activity.LoginActivity
import com.smile.qzclould.ui.user.loign.activity.ModifyPwdActivity
import com.smile.qzclould.ui.user.loign.fragment.PwdInputFragment
import com.smile.qzclould.ui.user.loign.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.frag_home_fourth.*

class HomeFourthFragment: BaseFragment() {
    private val mModel by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }
    private val mUserInfo by lazy { UserInfoManager.get().getUserInfo() }

    override fun getLayoutId(): Int {
        return R.layout.frag_home_fourth
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvNick.text = mUserInfo?.nickName
        mTvPhone.text = mUserInfo?.phone
    }

    override fun initListener() {
        mBtnLogout.setOnClickListener {
            showLoading()
            mModel.logout()
        }

        mBtnModifyPwd.setOnClickListener {
            showLoading()
            mModel.sendChangePasswordMessage()
        }
    }

    override fun initViewModel() {
        mModel.logoutResult.observe(this, Observer {
            UserInfoManager.get().logout()
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, App.instance.getString(R.string.logout_success))
            jumpActivity(LoginActivity::class.java)
            mActivity?.finish()
        })

        mModel.verifyCodeResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, mActivity?.getString(R.string.send_success)!!)
            val bundle = Bundle()
            bundle.putString("phone_info", it)
            bundle.putString("toolbar_title", App.instance.getString(R.string.modify_pwd))
            bundle.putInt("jump_type", PwdInputFragment.TYPE_MODIFY_PWD)
            jumpActivity(ModifyPwdActivity::class.java, bundle)
        })

        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_ERROR, it?.errorMessage!!)
        })
    }
}