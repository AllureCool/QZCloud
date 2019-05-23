package com.smile.qzclould.ui.user.loign.fragment

import android.os.Bundle
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import kotlinx.android.synthetic.main.fragment_regist_agreement.*

class RegistAgreementFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_regist_agreement
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        cb_agree.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_next.isEnabled = isChecked
        }

        iv_close.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        btn_next.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_registAgreementFragment_to_registInputPhonetFragment)
        }
    }
}