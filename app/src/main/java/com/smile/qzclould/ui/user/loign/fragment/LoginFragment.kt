package com.smile.qzclould.ui.user.loign.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.MainActivity
import com.smile.qzclould.ui.user.loign.activity.ModifyPwdActivity
import com.smile.qzclould.ui.user.loign.viewmodel.LoginViewModel
import com.smile.qzclould.utils.CommonUtils
import com.smile.qzclould.utils.DLog
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }

    private var mPhoneNum: String? = null
    private var mPassword: String? = null

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun getLayoutId(): Int {
        return R.layout.login_fragment
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBtnRegister.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_registerFragment)
        }

        mBtnLogin.setOnClickListener {
            showLoading()
            if (mPhoneNum != null && mPassword != null) {
                mModel.login(mPhoneNum!!, mPassword!!)
            }
        }

        mEtPhoneNum.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {
                mPhoneNum = s.toString()
                mBtnLogin.isEnabled = !TextUtils.isEmpty(mPhoneNum) && !TextUtils.isEmpty(mPassword)
            }
        })

        mEtPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {
                mPassword = CommonUtils.encodeMD5(mEtPassword.text.toString())
                DLog.i(mPassword + " -------------------------")
                mBtnLogin.isEnabled = !TextUtils.isEmpty(mPhoneNum) && !TextUtils.isEmpty(mPassword)
            }
        })

        mBtnForgetPwd.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_phoneInputFragment)
        }
    }

    override fun initViewModel() {
        mModel.loginResult.observe(this, Observer {
            stopLoading()
            UserInfoManager.get().saveUserInfo(it)
            showToast(Constants.TOAST_SUCCESS, App.instance.getString(R.string.login_success))
            val intent = Intent(mActivity, MainActivity::class.java)
            startActivity(intent)
            mActivity?.finish()
        })

        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_NORMAL, it?.errorMessage!!)
        })
    }
}
