package com.smile.qzclould.ui.user.loign.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.LinearLayout
import androidx.navigation.Navigation
import com.github.glomadrian.codeinputlib.CodeInputEditText
import com.github.glomadrian.codeinputlib.callback.CodeInputCallback
import com.gyf.barlibrary.ImmersionBar
import com.smile.qielive.common.BaseFragment
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.event.ModifyPwdEvent
import com.smile.qzclould.ui.user.loign.viewmodel.LoginViewModel
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.frag_verify_code.*

class VerifyCodeInputFragment : BaseFragment() {

    private var mCountDownTimer: CountDownTimer? = null
    private val mModel by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }
    private var mToolbarTitle: String? = null
    private var mCountryCode: String? = null
    private var mPhoneNum: String? = null
    private var mPhoneInfo: String? = null
    private var mVerifyCode: String? = null
    private var mJumpType = PwdInputFragment.TYPE_REGISTER

    override fun getLayoutId(): Int {
        return R.layout.frag_verify_code
    }

    override fun initData() {
        mCountryCode = arguments?.getString("country_code")
        mPhoneNum = arguments?.getString("phone_num")
        mPhoneInfo = arguments?.getString("phone_info")
        mToolbarTitle = arguments?.getString("toolbar_title")
        mJumpType = arguments?.getInt("jump_type", PwdInputFragment.TYPE_REGISTER)!!
    }

    override fun initView(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < 19) {
            statusView.visibility = View.GONE
        } else {
            statusView.visibility = View.VISIBLE
            val parm = statusView.layoutParams as LinearLayout.LayoutParams
            parm.height = ImmersionBar.getStatusBarHeight(mActivity)
            statusView.layoutParams = parm
        }
        mTvBarTitle.text = mToolbarTitle
        mCountDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mBtnGetCode.setTextColor(mActivity?.resources!!.getColor(R.color.color_gray_BFBFBF))
                mBtnGetCode?.isEnabled = false
                mBtnGetCode?.text = "${millisUntilFinished / 1000}秒"
            }

            override fun onFinish() {
                mBtnGetCode.setTextColor(mActivity?.resources!!.getColor(R.color.color_green_2EC17C))
                mBtnGetCode?.isEnabled = true
                mBtnGetCode?.text = mActivity?.getString(R.string.regain_verify_code)
            }
        }
        mCountDownTimer?.start()

    }

    override fun initListener() {
        mBackBtn.setOnClickListener {
            when(mJumpType) {
                PwdInputFragment.TYPE_REGISTER -> Navigation.findNavController(it).navigateUp()
                PwdInputFragment.TYPE_MODIFY_PWD -> mActivity?.finish()
                PwdInputFragment.TYPE_RESET_PWD -> Navigation.findNavController(it).navigateUp()
            }
        }
        mBtnGetCode.setOnClickListener {
            showLoading()
            when(mJumpType) {
                PwdInputFragment.TYPE_REGISTER -> {
                    if(mCountryCode != null && mPhoneNum != null) {
                        mModel.sendRegisterMessage(mCountryCode!!, mPhoneNum!!)
                    }
                }

                PwdInputFragment.TYPE_MODIFY_PWD -> {
                    mModel.sendChangePasswordMessage()
                }

                PwdInputFragment.TYPE_RESET_PWD -> {
                    if(mPhoneNum != null) {
                        mModel.sendForgetPwdMessage(mPhoneNum!!)
                    }
                }
            }
        }
        mCodeInput.setCodeInputListener(object : CodeInputCallback<CodeInputEditText> {
            override fun onInputFinish(ci: CodeInputEditText, inputResult: String) {
                mVerifyCode = inputResult
                mBtnNext.isEnabled = true

            }
            override fun onInput(ci: CodeInputEditText, currentChar: Char?) {
                mBtnNext.isEnabled = false
            }
        })
        mBtnNext.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("verify_code", mVerifyCode)
            bundle.putString("phone_num", mPhoneNum)
            bundle.putString("phone_info", mPhoneInfo)
            bundle.putInt("jump_type", mJumpType)
            when(mJumpType) {
                PwdInputFragment.TYPE_REGISTER -> {
                    Navigation.findNavController(it).navigate(R.id.action_verifyCodeInputFragment_to_pwdInputFragment, bundle)
                }
                PwdInputFragment.TYPE_MODIFY_PWD -> {
                    Navigation.findNavController(it).navigate(R.id.action_verifyCodeInputFragment3_to_pwdInputFragment3, bundle)
                }
                PwdInputFragment.TYPE_RESET_PWD -> {
                    Navigation.findNavController(it).navigate(R.id.action_verifyCodeInputFragment_to_pwdInputFragment, bundle)
                }
            }

        }
    }

    override fun initViewModel() {
        mModel.verifyCodeResult.observe(this, Observer {
            stopLoading()
            mCountDownTimer?.start()
            showToast(Constants.TOAST_SUCCESS, mActivity?.getString(R.string.send_success)!!)
        })

        mModel.errorStatus.observe(this, Observer {
            stopLoading()
            showToast(Constants.TOAST_NORMAL, it?.errorMessage!!)
        })
    }

    override fun initEvent() {
        RxBus.toObservable(ModifyPwdEvent::class.java)
                .subscribe {
                    mPhoneInfo = it.phoneInfo
                    mToolbarTitle = it.toobarTitle
                    mJumpType = it.jumpType
                    mTvBarTitle.text = mToolbarTitle
                }
                .autoDispose()
    }

    override fun onDestroyView() {
        mCountDownTimer?.cancel()
        super.onDestroyView()
    }
}