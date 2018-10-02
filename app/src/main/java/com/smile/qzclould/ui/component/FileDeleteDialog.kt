package com.smile.qzclould.ui.component

import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.utils.ViewUtils
import kotlinx.android.synthetic.main.dialog_bottom_delete.*

class FileDeleteDialog: BaseDialogFragment() {

    private var mOnDialogClickListener: OnDialogClickListener? = null

    override fun setLayoutId(): Int {
        return R.layout.dialog_bottom_delete
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.BOTTOM)
        mWindow.setWindowAnimations(R.style.MyBottomDialog)
        mWindow.setLayout(mWidth, ViewUtils.dip2px(60f).toInt())
    }

    override fun initView() {
        rl_delete.setOnClickListener {
            mOnDialogClickListener?.onDeleteClick()
            dismiss()
        }
    }

    fun setOnDialogClickListener(onDialogClickListener: OnDialogClickListener) {
        mOnDialogClickListener = onDialogClickListener
    }

    interface OnDialogClickListener  {
        fun onDeleteClick()
    }
}