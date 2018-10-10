package com.smile.qzclould.ui.transfer.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.event.RefreshOfflineTaskListEvent
import com.smile.qzclould.ui.transfer.viewmodel.TransferViewModel
import com.smile.qzclould.utils.RxBus
import kotlinx.android.synthetic.main.dialog_add_download_task.*



class AddTaskDialog: BaseDialogFragment() {

    private val mModel by lazy { ViewModelProviders.of(this).get(TransferViewModel::class.java) }
    private val cm by lazy { mActivity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }

    override fun setLayoutId(): Int {
        return R.layout.dialog_add_download_task
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogFragmentStyle)
    }

    override fun onStart() {
        super.onStart()
        mWindow.setGravity(Gravity.BOTTOM)
        mWindow.setWindowAnimations(R.style.MyBottomDialog)
        mWindow.setLayout(mWidth, mHeight * 2 / 3)
    }

    override fun initView() {
        mIvClose.setOnClickListener {
            dismiss()
        }

        mBtnConfirm.setOnClickListener {
            mModel.parseUrl(mEtDownloadUrl.text.toString())
        }

        mQuickPaste.setOnClickListener {
            mEtDownloadUrl.setText(getClipboardText())
        }
        initViewModel()
    }

    fun initViewModel() {
        mModel.parseUrlResult.observe(this, Observer {
            mModel.offlineDownloadStart(it!!.taskHash, "", arrayOf())
        })

        mModel.offlineDownloadResult.observe(this, Observer {
            RxBus.post(RefreshOfflineTaskListEvent())
            showToast(Constants.TOAST_SUCCESS, getString(R.string.already_add_to_offlin_task))
            dismiss()
        })
    }

    private fun getClipboardText(): String? {
        val data = cm.primaryClip
        val item = data?.getItemAt(0)
        return item?.text.toString()
    }
}