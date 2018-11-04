package com.smile.qzclould.ui.user.info

import androidx.navigation.Navigation.findNavController
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R

class UserInfoActivity: BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.activity_user_info
    }

    override fun onSupportNavigateUp() =
            findNavController(this, R.id.fragment).navigateUp()
}