package com.smile.qzclould.ui.cloud.fragment

import android.os.Bundle
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.db.User
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.user.loign.LoginActivity
import com.smile.qzclould.utils.DLog
import kotlinx.android.synthetic.main.frag_home_first.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class HomeFirstFragment: BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.frag_home_first
    }

    override fun initView(savedInstanceState: Bundle?) {

        DLog.i(UserInfoManager.get().hasLogin().toString() + "--------------------")
        mTvUpload.setOnClickListener {
            jumpActivity(LoginActivity::class.java)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}