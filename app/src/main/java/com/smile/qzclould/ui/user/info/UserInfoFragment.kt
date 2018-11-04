package com.smile.qzclould.ui.user.info

import android.os.Bundle
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.event.RefreshUserInfoEvent
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_user_info.*

class UserInfoFragment: BaseFragment() {

    private var mUserInfo: UserInfoBean? = null

    override fun getLayoutId(): Int {
        return R.layout.frag_user_info
    }

    override fun initData() {
        mUserInfo = UserInfoManager.get().getUserInfo()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvNick.text = mUserInfo?.nickName
    }

    override fun onResume() {
        super.onResume()
        mUserInfo = UserInfoManager.get().getUserInfo()
        mTvNick?.text = mUserInfo?.nickName
    }

    override fun initListener() {
        mLlNickname.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_userInfoFragment_to_modifyNickNameFragment)
        }

        mIvBack.setOnClickListener {
            mActivity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}