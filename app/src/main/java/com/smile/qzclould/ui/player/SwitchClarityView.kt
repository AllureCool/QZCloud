package com.smile.qzclould.ui.player

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.utils.ViewUtils

class SwitchClarityView: BaseDialogFragment() {

    override fun setLayoutId(): Int {
        return R.layout.view_clarity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogFragmentNoBackgroundStyle)
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.TOP or Gravity.END)
        mWindow.setWindowAnimations(R.style.RightDialog)
        mWindow.setLayout(ViewUtils.dip2px(248f).toInt(), mHeight)
    }

    override fun initView() {

    }
}