package com.smile.qzclould.ui.preview.player.uicomponent

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import com.smile.qzclould.R
import com.smile.qzclould.common.base.BaseDialogFragment
import com.smile.qzclould.ui.preview.player.bean.VideoDetailBean
import com.smile.qzclould.ui.preview.player.adapter.ClarityAdapter
import com.smile.qzclould.utils.ViewUtils
import kotlinx.android.synthetic.main.view_clarity.*

class SwitchClarityView : BaseDialogFragment() {

    private lateinit var mVideoDetail: VideoDetailBean
    private var mAdapter: ClarityAdapter? = null
    private var mOnClaritySelectedListener: OnClaritySelectedListener? = null

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

    override fun initData() {
        mVideoDetail = arguments!!.getSerializable("video_infos") as VideoDetailBean
    }

    override fun initView() {
        mAdapter = ClarityAdapter()
        mRvClarity.layoutManager = LinearLayoutManager(mActivity)
        mAdapter?.bindToRecyclerView(mRvClarity)
        mAdapter?.setNewData(mVideoDetail.preview)

        mAdapter?.setOnItemClickListener { adapter, view, position ->
            if(!(adapter.getItem(position) as VideoDetailBean.VideoInfo).isPlay) {
                mOnClaritySelectedListener?.onClaritySelected(adapter.getItem(position) as VideoDetailBean.VideoInfo)
                dismiss()
            }
        }
    }

    fun setOnClaritySelectedListener(onClaritySelectedListener: OnClaritySelectedListener) {
        mOnClaritySelectedListener = onClaritySelectedListener
    }

    interface OnClaritySelectedListener {
        fun onClaritySelected(info: VideoDetailBean.VideoInfo)
    }
}