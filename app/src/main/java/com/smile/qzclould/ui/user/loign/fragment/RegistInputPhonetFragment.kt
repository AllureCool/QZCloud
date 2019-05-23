package com.smile.qzclould.ui.user.loign.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.event.SelectCountryCodeEvent
import com.smile.qzclould.ui.user.loign.bean.CountryCodeBean
import com.smile.qzclould.ui.user.loign.dialog.SelectCountryCodeDialog
import com.smile.qzclould.ui.user.loign.viewmodel.UserViewModel
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.fragment_regist_input_phone.*

class RegistInputPhonetFragment : BaseFragment() {

    private var mCountryCode = "86"
    private val mModel by lazy { ViewModelProviders.of(this).get(UserViewModel::class.java) }

    override fun getLayoutId(): Int {
        return R.layout.fragment_regist_input_phone
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
        iv_close.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
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
            showLoading()
            mModel.sendRegisterMessage(mCountryCode, et_phone1.text.toString())
        }
    }

    override fun initViewModel() {
        mModel.verifyCodeResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, getString(R.string.send_success))
            val bundle = Bundle()
            bundle.putString("phone_num", et_phone1.text.toString())
            bundle.putString("country_code", mCountryCode)
            bundle.putString("phone_info", it)
            Navigation.findNavController(et_phone1).navigate(R.id.action_registInputPhonetFragment_to_registInputPasswordFragment, bundle)
        })
        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_NORMAL, it?.errorMessage!!)
        })
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