package com.smile.qzclould.ui.user.loign.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.MainActivity
import com.smile.qzclould.ui.user.loign.dialog.UserOnlineDialog
import com.smile.qzclould.ui.user.loign.viewmodel.UserViewModel
import com.smile.qzclould.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_login_by_password.*

class LoginByPasswordFragment: BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(UserViewModel::class.java) }

    override fun getLayoutId(): Int {
        return R.layout.fragment_login_by_password
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        tv_login_by_phone.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        et_phone.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn_login.isEnabled = et_phone?.text.toString().isNotEmpty() && et_pwd?.text.toString().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        et_pwd.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn_login.isEnabled = et_phone?.text.toString().isNotEmpty() && et_pwd?.text.toString().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        tv_regist.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginByPasswordFragment_to_registAgreementFragment2)
        }

        btn_login.setOnClickListener {
            showLoading()
            mModel.login(et_phone.text.toString(), CommonUtils.encodeMD5(et_pwd.text.toString()))
        }
    }

    override fun initViewModel() {
        mModel.loginResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, App.instance.getString(R.string.login_success))
            UserInfoManager.get().saveUserInfo(it)
            val intent = Intent(mActivity, MainActivity::class.java)
            startActivity(intent)
            mActivity?.finish()
        })

        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_NORMAL, it?.errorMessage!!)
            if(it.errorCode == 16) {
                val dialog = UserOnlineDialog()
                dialog.show(childFragmentManager, "user_online_dialog")
            } else {
                UserInfoManager.get().logout()
            }
        })
    }

}