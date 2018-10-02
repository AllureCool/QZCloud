package com.smile.qzclould.ui.cloud.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.gyf.barlibrary.ImmersionBar
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.event.*
import com.smile.qzclould.utils.RxBus
import com.smile.qzclould.utils.ViewUtils
import kotlinx.android.synthetic.main.dialog_file_operation.*

class FileOperationDialog: BaseDialogFragment() {
    private val mTopEnterAnimator by lazy { ObjectAnimator.ofFloat(mFlTop, "translationY", -ViewUtils.dip2px(60f), 0f) }
    private val mTopExitAnimator by lazy { ObjectAnimator.ofFloat(mFlTop, "translationY", 0f, -ViewUtils.dip2px(60f)) }
    private val mBottomEnterAnimator by lazy { ObjectAnimator.ofFloat(mFlBottom, "translationY", ViewUtils.dip2px(60f), 0f) }
    private val mBottomExitAnimator by lazy { ObjectAnimator.ofFloat(mFlBottom, "translationY", 0f, ViewUtils.dip2px(60f)) }

    private val mEnterAnimatorSet by lazy { AnimatorSet() }
    private val mExitAnimatorSet by lazy { AnimatorSet() }

    private var mShowDownloadBtn = true

    override fun setLayoutId(): Int {
        return R.layout.dialog_file_operation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogFragmentNoBackgroundStyle)
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.CENTER)
        mWindow.setLayout(mWidth, mHeight)
    }

    override fun initView() {

        mShowDownloadBtn = arguments!!.getBoolean("show_download_btn")

        if(mShowDownloadBtn) {
            mLlDownload.visibility = View.VISIBLE
        } else {
            mLlDownload.visibility = View.GONE
        }

        val layoutParms = mFlTop.layoutParams as LinearLayout.LayoutParams
        layoutParms.height = ImmersionBar.getStatusBarHeight(mActivity) + ViewUtils.dip2px(45f).toInt()
        mFlTop.layoutParams = layoutParms

        mTempView.setOnTouchListener { v, event ->
            RxBus.post(ClickThroughEvent(event))
            return@setOnTouchListener true
        }

        mTvCancel.setOnClickListener {
            RxBus.post(FileControlEvent(EVENT_CANCEl))
            dismissDialog()
        }
        mTvSelectAll.setOnClickListener {
            RxBus.post(FileControlEvent(EVENT_SELECTALL))
        }
        mLlDownload.setOnClickListener {
            RxBus.post(FileControlEvent(EVENT_DOWNLOAD))
            dismissDialog()
            showToast(Constants.TOAST_SUCCESS, mActivity.getString(R.string.downloading))
        }

        mLlDelete.setOnClickListener {
            RxBus.post(FileControlEvent(EVENT_DELETE))
            dismissDialog()
        }

        mEnterAnimatorSet.play(mTopEnterAnimator).with(mBottomEnterAnimator)
        mEnterAnimatorSet.duration = 200
        mEnterAnimatorSet.start()

        mExitAnimatorSet.play(mTopExitAnimator).with(mBottomExitAnimator)
        mExitAnimatorSet.duration = 200
    }

    fun dismissDialog() {
        mExitAnimatorSet.start()

        mExitAnimatorSet.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                dismiss()
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {

            }
        })
    }

    override fun onDismiss(dialog: DialogInterface?) {
        RxBus.post(FileControlEvent(EVENT_CANCEl))
        super.onDismiss(dialog)
    }
}