package com.smile.qzclould.ui.user.loign.fragment

import android.os.Bundle
import android.view.View
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import kotlinx.android.synthetic.main.fragment_login_by_phone.*

class LoginByPhoneFragment: BaseFragment() {


    override fun getLayoutId(): Int {
        return R.layout.fragment_login_by_phone
    }

    override fun initView(savedInstanceState: Bundle?) {
        cl_login_first_step.visibility = View.GONE
        cl_login_second_step.visibility = View.VISIBLE
    }

    override fun initData() {
        val phoneNum = arguments?.getString("phone_num")
        et_phone2.setText(phoneNum)
    }
}