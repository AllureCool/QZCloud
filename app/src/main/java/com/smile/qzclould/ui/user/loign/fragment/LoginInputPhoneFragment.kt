package com.smile.qzclould.ui.user.loign.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.event.SelectCountryCodeEvent
import com.smile.qzclould.ui.user.loign.bean.CountryCodeBean
import com.smile.qzclould.ui.user.loign.dialog.SelectCountryCodeDialog
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.fragment_login_by_phone.*

class LoginInputPhoneFragment : BaseFragment() {

    private var mCountryCode = "86"

    override fun getLayoutId(): Int {
        return R.layout.fragment_login_by_phone
    }

    override fun initView(savedInstanceState: Bundle?) {
        tv_area_code.text = "中国(+86)"
    }

    override fun initListener() {
        tv_area_code.setOnClickListener {
            val areaCodeDialog = SelectCountryCodeDialog()
            val bundle = Bundle()
            bundle.putString("country_code", mCountryCode)
            areaCodeDialog.arguments = bundle
            areaCodeDialog.show(childFragmentManager, "selectAreaCodeDialog")
        }
        tv_login_by_username.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginInputPhoneFragment_to_loginByPasswordFragment)
        }
        et_phone1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn_next.isEnabled = !TextUtils.isEmpty(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        btn_next.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("phone_num", et_phone1.text.toString())
            bundle.putString("country_code", mCountryCode)
            Navigation.findNavController(it).navigate(R.id.action_loginInputPhoneFragment_to_loginByPhoneFragment, bundle)
            //隐藏软键盘
            val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        tv_regist.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginInputPhoneFragment_to_registAgreementFragment)
        }
    }

    override fun initEvent() {
        RxBus.toObservable(SelectCountryCodeEvent::class.java)
                .subscribe{
                    mCountryCode = it.codeBean.countryCode
                    updateCode(it.codeBean)
                }
                .autoDispose()
    }

    private fun updateCode(codeBean: CountryCodeBean) {
        tv_area_code?.text = "${codeBean.countryName}(+${codeBean.countryCode})"
    }
}