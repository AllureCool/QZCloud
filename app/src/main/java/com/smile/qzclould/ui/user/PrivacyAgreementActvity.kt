package com.smile.qzclould.ui.user

import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import kotlinx.android.synthetic.main.activity_privacy_agreement.*

class PrivacyAgreementActvity: BaseActivity() {

    override fun setLayoutId(): Int {
        return R.layout.activity_privacy_agreement
    }

    override fun initView() {
        mIvBack.setOnClickListener {
            finish()
        }
        mWeb.loadUrl("file:///android_asset/privacy_agreement.html")
    }
}