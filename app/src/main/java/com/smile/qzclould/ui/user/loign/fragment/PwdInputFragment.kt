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
import com.smile.qzclould.ui.user.loign.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.frag_pwd_input.*
import java.util.*

class PwdInputFragment : BaseFragment() {

    companion object {
        const val TYPE_REGISTER = 0
        const val TYPE_MODIFY_PWD = 1
    }

    private val mModel by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }
    private var mVerifyCode: String? = null
    private var mPhoneNum: String? = null
    private var mPhoneInfo: String? = null
    private var mJumpType = TYPE_REGISTER

    override fun getLayoutId(): Int {
        return R.layout.frag_pwd_input
    }

    override fun initData() {
        mVerifyCode = arguments?.getString("verify_code")
        mPhoneNum = arguments?.getString("phone_num")
        mPhoneInfo = arguments?.getString("phone_info")
        mJumpType = arguments?.getInt("jump_type", TYPE_REGISTER)!!
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mEtPwd.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val length = mEtPwd.text.toString().length
                mBtnComplete.isEnabled = length >= 6
            }
        })


        mBtnComplete.setOnClickListener {
            showLoading()
            if(mPhoneInfo != null && mVerifyCode != null) {
                when {
                    mJumpType == TYPE_REGISTER ->  mModel.register(mPhoneInfo!!, mVerifyCode!!, randomNickname(10), mEtPwd.text.toString())
                    mJumpType == TYPE_MODIFY_PWD -> mModel.changePasswordByMessage(mPhoneInfo!!, mVerifyCode!!, mEtPwd.text.toString())
                }
            }

        }
    }

    override fun initViewModel() {
        mModel.loginResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, App.instance.getString(R.string.register_success))
            UserInfoManager.get().saveUserInfo(it)
            val intent = Intent(mActivity, MainActivity::class.java)
            startActivity(intent)
            mActivity?.finish()
        })

        mModel.modifyPwdResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, App.instance.getString(R.string.modify_pwd_success))
            mActivity?.finish()
        })

        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_ERROR, it?.errorMessage!!)
        })
    }

    /**
     * 随机生成一个昵称
     */
    private fun randomNickname(length: Int): String {
        val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        val buf = StringBuffer()
        for (i in 0 until length) {
            val num = random.nextInt(62)
            buf.append(str[num])
        }

        return buf.toString()
    }
}