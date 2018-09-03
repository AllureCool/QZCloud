package com.smile.qzclould.ui.user.loign.activity

import androidx.navigation.Navigation
import com.gyf.barlibrary.ImmersionBar
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R

class LoginActivity : BaseActivity() {

    override fun setLayoutId(): Int {
        return R.layout.login_activity
    }

    override fun initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar?.statusBarDarkFont(true, 0.2f)
        mImmersionBar?.statusBarColor(R.color.color_white_ffffff)
        mImmersionBar?.fitsSystemWindows(false)
        mImmersionBar?.init()
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.loginFragment).navigateUp()
    }
}
