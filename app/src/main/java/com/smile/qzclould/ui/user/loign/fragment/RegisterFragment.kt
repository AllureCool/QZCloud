package com.smile.qzclould.ui.user.loign.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.Navigation
import com.gyf.barlibrary.ImmersionBar
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.event.SelectCountryCodeEvent
import com.smile.qzclould.ui.user.loign.dialog.SelectCountryCodeDialog
import com.smile.qzclould.ui.user.loign.viewmodel.LoginViewModel
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_register.*

class RegisterFragment: BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }
    private var mCountryCode = "86"
    private var mPhoneNum = ""

    override fun getLayoutId(): Int {
        return R.layout.frag_register
    }

    override fun initView(savedInstanceState: Bundle?) {
        if(Build.VERSION.SDK_INT < 19) {
            statusView.visibility = View.GONE
        } else {
            statusView.visibility = View.VISIBLE
            val parm = statusView.layoutParams as ConstraintLayout.LayoutParams
            parm.height = ImmersionBar.getStatusBarHeight(mActivity)
            statusView.layoutParams = parm
        }

        mBackBtn.setOnClickListener { Navigation.findNavController(it).navigateUp() }

        mBtnNext.setOnClickListener {
            showLoading()
            mPhoneNum = mEtPhoneNum.text.toString()
            mModel.sendRegisterMessage(mCountryCode, mEtPhoneNum.text.toString())
        }

        mAreaCode.setOnClickListener {
            val dialog = SelectCountryCodeDialog()
            val bundle = Bundle()
            bundle.putString("country_code", mCountryCode)
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "select_country_code")
        }

        mEtPhoneNum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                mBtnNext.isEnabled = mEtPhoneNum.text.toString().isNotEmpty()

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        updateCode(mCountryCode)
    }

    override fun initViewModel() {
        mModel.verifyCodeResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, mActivity?.getString(R.string.send_success)!!)
            val bundle = Bundle()
            bundle.putString("country_code", mCountryCode)
            bundle.putString("phone_num", mPhoneNum)
            bundle.putString("phone_info", it)
            bundle.putString("toolbar_title", App.instance.getString(R.string.regist_title))
            bundle.putInt("jump_type", PwdInputFragment.TYPE_REGISTER)
            Navigation.findNavController(mBtnNext).navigate(R.id.action_registerFragment_to_verifyCodeInputFragment, bundle)
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
                    updateCode(mCountryCode)
                }
                .autoDispose()
    }

    private fun updateCode(code: String) {
        mAreaCode.text = "+${mCountryCode}"
    }
}