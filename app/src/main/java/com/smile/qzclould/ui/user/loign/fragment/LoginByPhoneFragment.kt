package com.smile.qzclould.ui.user.loign.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants.TOAST_ERROR
import com.smile.qzclould.common.Constants.TOAST_SUCCESS
import com.smile.qzclould.ui.user.loign.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_login_by_phone.*

class LoginByPhoneFragment: BaseFragment() {
    private var mPhoneNum: String? = null
    private var mCountryCode: String? = null
    private var mPhoneInfo: String? = null
    private val mModel by lazy { ViewModelProviders.of(this).get(UserViewModel::class.java) }
    private var mCountDownTimer: CountDownTimer? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_login_by_phone
    }

    override fun initView(savedInstanceState: Bundle?) {
        cl_login_first_step.visibility = View.GONE
        cl_login_second_step.visibility = View.VISIBLE
        mCountDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_get_vcode.isEnabled = false
                tv_get_vcode.setTextColor(resources.getColor(R.color.color_gray_919191))
                tv_get_vcode.text = "${millisUntilFinished / 1000}秒后重试"
            }

            override fun onFinish() {
                tv_get_vcode.isEnabled = true
                tv_get_vcode.setTextColor(resources.getColor(R.color.color_black_404040))
                tv_get_vcode.text = getString(R.string.login_get_vcode)
            }
        }
    }

    override fun initData() {
        mPhoneNum = arguments?.getString("phone_num")
        mCountryCode = arguments?.getString("country_code")
        et_phone2.setText("+$mCountryCode$mPhoneNum")
    }

    override fun initListener() {
        tv_get_vcode.setOnClickListener {
            mModel.sendLoginMessage(mCountryCode!!, mPhoneNum!!)
        }
    }

    override fun initViewModel() {
        mModel.loginMsgRsp.observe(this, Observer {
            stopLoading()
            showToast(TOAST_SUCCESS, getString(R.string.send_success))
            mPhoneInfo = it
            mCountDownTimer?.start()

        })

        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(TOAST_ERROR, it?.errorMessage!!)
        })
    }

}