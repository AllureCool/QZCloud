package com.smile.qzclould.ui.cloud.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.ui.transfer.dialog.SelectDownloadPathDialog
import kotlinx.android.synthetic.main.dialog_file_more_operation.*

class FileMoreOptDialog: BaseDialogFragment() {

    private var mEventId = 0

    override fun setLayoutId(): Int {
        return R.layout.dialog_file_more_operation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogFragmentStyle)
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.BOTTOM)
        mWindow.setWindowAnimations(R.style.MyBottomDialog)
        mWindow.setLayout(mWidth, mHeight * 1 / 3)
    }

    override fun initData() {
        if(arguments != null) {
            mEventId = arguments!!.getInt("eventId", 0)
        }
    }

    override fun initView() {
        mTvCancel.setOnClickListener {
            dismiss()
        }

        mTvMove.setOnClickListener {
            val selectPathDialog = SelectDownloadPathDialog()
            val ft = childFragmentManager.beginTransaction()
            val bundle = Bundle()
            bundle.putInt("file_opt", 1)
            bundle.putInt("eventId", mEventId)
            selectPathDialog.arguments = bundle
            ft.add(selectPathDialog, selectPathDialog?.javaClass?.simpleName)
            ft.commitAllowingStateLoss()
//            dismiss()
        }

        mTvCopy.setOnClickListener {
            val selectPathDialog = SelectDownloadPathDialog()
            val ft = childFragmentManager.beginTransaction()
            val bundle = Bundle()
            bundle.putInt("file_opt", 2)
            bundle.putInt("eventId", mEventId)
            selectPathDialog.arguments = bundle
            ft.add(selectPathDialog, selectPathDialog?.javaClass?.simpleName)
            ft.commitAllowingStateLoss()
//            dismiss()
        }
    }

}