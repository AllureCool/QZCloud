package com.smile.qzclould.ui.user.loign.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.ui.user.loign.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.frag_phone_input.*

class PhoneInputFragment : BaseFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(UserViewModel::class.java) }

    override fun getLayoutId(): Int {
        return R.layout.frag_phone_input
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mEtPhoneNum.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val length = mEtPhoneNum.text.toString().length
                mBtnNext.isEnabled = length >= 6
            }
        })


        mBtnNext.setOnClickListener {
            showLoading()
            mModel.sendForgetPwdMessage(mEtPhoneNum.text.toString())
        }
    }

    override fun initViewModel() {
        mModel.verifyCodeResult.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_SUCCESS, mActivity?.getString(R.string.send_success)!!)
            val bundle = Bundle()
            bundle.putString("phone_info", it)
            bundle.putString("toolbar_title", App.instance.getString(R.string.reset_pwd))
            bundle.putInt("jump_type", PwdInputFragment.TYPE_RESET_PWD)
            Navigation.findNavController(mEtPhoneNum).navigate(R.id.action_phoneInputFragment_to_verifyCodeInputFragment, bundle)
        })
    }
}