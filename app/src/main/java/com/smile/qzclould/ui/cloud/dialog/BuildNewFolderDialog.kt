package com.smile.qzclould.ui.cloud.dialog

import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.App
import com.smile.qzclould.common.base.BaseDialogFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_new_folder.*

class BuildNewFolderDialog: BaseDialogFragment() {

    private var mListener: DialogButtonClickListener? = null

    override fun setLayoutId(): Int {
        return R.layout.dialog_new_folder
    }

    override fun onStart() {
        super.onStart()
        super.onStart()
        mWindow.setGravity(Gravity.CENTER)
        mWindow.setWindowAnimations(R.style.MyBottomDialog)
        mWindow.setLayout(mWidth, mHeight)
    }


    override fun initView() {
        dialogContainer.setOnClickListener { dismiss() }
        mTvCancel.setOnClickListener { dismiss() }
        mTvConfirm.setOnClickListener {
            if(mEtFolderName.text.toString().isEmpty()) {
                Toasty.info(App.instance, App.instance.getString(R.string.folder_name_not_empty)).show()
            } else {
                mListener?.onConfirmClick(mEtFolderName.text.toString())
                mEtFolderName.setText("")
                dismiss()
            }
        }
    }

    fun setDialogClickListener(listener: DialogButtonClickListener) {
        mListener = listener
    }

    interface DialogButtonClickListener {
        fun onConfirmClick(folderName: String)
    }
}