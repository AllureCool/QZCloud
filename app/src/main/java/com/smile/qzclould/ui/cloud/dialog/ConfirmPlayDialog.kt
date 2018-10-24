package com.smile.qzclould.ui.cloud.dialog

import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_confirm_play.*

class ConfirmPlayDialog: BaseDialogFragment() {

    private var mListener: DialogButtonClickListener? = null

    override fun setLayoutId(): Int {
        return R.layout.dialog_confirm_play
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.CENTER)
        mWindow.setWindowAnimations(R.style.MyBottomDialog)
        mWindow.setLayout(mWidth, mHeight)
    }

    override fun initView() {
        dialogContainer.setOnClickListener {
            dismiss()
        }
        mTvCancel.setOnClickListener {
            dismiss()
        }
        mTvConfirm.setOnClickListener {
            dismiss()
            mListener?.onConfirmClick()
        }
    }

    fun setDialogClickListener(listener: DialogButtonClickListener) {
        mListener = listener
    }

    interface DialogButtonClickListener {
        fun onConfirmClick()
    }
}