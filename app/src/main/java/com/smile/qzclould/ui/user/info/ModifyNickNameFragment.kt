package com.smile.qzclould.ui.user.info

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.navigation.Navigation
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.event.RefreshUserInfoEvent
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.user.loign.viewmodel.UserViewModel
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_modify_nick_name.*

class ModifyNickNameFragment: BaseFragment() {

    private val mUserInfo by lazy { UserInfoManager.get().getUserInfo() }
    private val mModel by lazy { ViewModelProviders.of(this).get(UserViewModel::class.java) }
    private var mNickName: String? = null

    override fun getLayoutId(): Int {
        return R.layout.frag_modify_nick_name
    }

    override fun initView(savedInstanceState: Bundle?) {
        mEtNick.setText(mUserInfo?.nickName)
    }

    override fun initViewModel() {
        mModel.modifyNameResult.observe(this, Observer {
            mUserInfo?.nickName = mNickName!!
            UserInfoManager.get().saveUserInfo(mUserInfo)
            stopLoading()
            Navigation.findNavController(mEtNick).popBackStack()
            RxBus.post(RefreshUserInfoEvent())
        })

        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_NORMAL, getString(R.string.error_modify_nick))
        })
    }

    override fun initListener() {
        mIvBack.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        mIvClear.setOnClickListener {
            mEtNick.setText("")
        }

        mTvSave.setOnClickListener {
            if(mEtNick.text.toString().isEmpty()) {
                showToast(Constants.TOAST_NORMAL, getString(R.string.please_input_nick_name))
            } else {
                mNickName = mEtNick.text.toString()
                showLoading()
                mModel.modifyName(mNickName!!)
            }
        }
    }
}