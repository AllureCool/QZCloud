package com.smile.qzclould.ui.user.loign.activity

import android.os.Handler
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.event.ModifyPwdEvent
import com.smile.qzclould.ui.user.loign.fragment.PwdInputFragment
import com.smile.qzclould.utils.RxBus

class ModifyPwdActivity : BaseActivity() {

    private var mToolbarTitle: String? = null
    private var mPhoneInfo: String? = null
    private var mJumpType = PwdInputFragment.TYPE_REGISTER

    private val navController by lazy {  Navigation.findNavController(this, R.id.modify_pwd_fragment) }
    override fun setLayoutId(): Int {
        return R.layout.act_modify_pwd
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun initData() {
        mToolbarTitle = intent.getBundleExtra("bundle_extra").getString("toolbar_title")
        mPhoneInfo = intent.getBundleExtra("bundle_extra").getString("phone_info")
        mJumpType = intent.getBundleExtra("bundle_extra").getInt("jump_type")
    }

    override fun initView() {

        Handler().postDelayed({
            RxBus.post(ModifyPwdEvent(mPhoneInfo!!, App.instance.getString(R.string.modify_pwd), mJumpType))
        }, 200)
    }
}