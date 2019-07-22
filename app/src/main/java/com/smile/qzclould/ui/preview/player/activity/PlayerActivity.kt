package com.smile.qzclould.ui.preview.player.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.FrameLayout
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.preview.PreviewViewModel
import com.smile.qzclould.ui.preview.player.uicomponent.SwitchClarityView
import com.smile.qzclould.ui.preview.player.bean.VideoDetailBean
import com.smile.qzclould.ui.preview.video.VideoBeanV2
import com.smile.qzclould.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity: BaseActivity(){

    private val mModel by lazy { ViewModelProviders.of(this).get(PreviewViewModel::class.java) }
    private lateinit var mPath: String
    private var mIsLocalVideo: Boolean = false
    private var mFirstLoad: Boolean = true
    private var mHasPreview: Boolean = true
    private var mSwitchClarityView: SwitchClarityView? = null
    private var mVideoDetail: VideoBeanV2? = null

    override fun setLayoutId(): Int {
        return R.layout.activity_player
    }

    override fun initData() {
        mIsLocalVideo = intent.getBundleExtra("bundle_extra").getBoolean("isLocal")
        mPath = intent.getBundleExtra("bundle_extra").getString("path")
        mHasPreview = intent.getBundleExtra("bundle_extra").getBoolean("hasPreview")
        if(mIsLocalVideo) {
            play(mPath)
        } else {
            if(mHasPreview) {
                mModel.getMediaInfo(mPath)
            } else {
                mModel.loadFileDetail(mPath)
            }
        }
    }

    override fun initView() {
        mVideoView.layoutParams = FrameLayout.LayoutParams(-1, ViewUtils.getScreenWidth() * 9 / 16)
        mVideoView.enterFullMode = 3
        mVideoView.isWindowGesture = true
        mVideoView.enterWindowFullscreen()

        mVideoView.setOnPlayerControlListener {
//            if(mSwitchClarityView == null) {
//                mSwitchClarityView = SwitchClarityView()
//            }
//
//            if(!mSwitchClarityView!!.isAdded) {
//                val bundle = Bundle()
//                bundle.putSerializable("video_infos", mVideoDetail)
//                mSwitchClarityView!!.arguments = bundle
//                mSwitchClarityView!!.show(supportFragmentManager, "clarity_view")
//            }
//            mSwitchClarityView?.setOnClaritySelectedListener(object : SwitchClarityView.OnClaritySelectedListener {
//                override fun onClaritySelected(info: VideoDetailBean.VideoInfo) {
//                    for (item in mVideoDetail!!.preview) {
//                        item.isPlay = false
//                    }
//                    info.isPlay = true
//                    play(info.url + "?token=" + UserInfoManager.get().getUserToken())
//                }
//            })
        }


    }

    override fun initViewModel() {
        mModel.MediaInfoResult.observe(this, Observer {
            mVideoDetail = it
//            if(!it!!.preview.isEmpty()) {
                mVideoView.showClarityBtn(false)
                play(it?.previewHlsAddress + "?token=" + UserInfoManager.get().getUserToken())
//                it.preview[0].isPlay = true
//            }
        })

        mModel.fileDetail.observe(this, Observer {
            mVideoView.showClarityBtn(false)
            play(it?.downloadAddress!!)
        })
    }

    private fun play(url: String) {
        if(mIsLocalVideo) {
            mVideoView.setiMediaControl(1)
        }
        mVideoView.setUp(url, "")
        mVideoView.play()
    }

    override fun onResume() {
        super.onResume()
        if (!mFirstLoad) {
            mVideoView.play()
        }
        mFirstLoad = false
    }

    override fun onStop() {
        super.onStop()
        mVideoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.release()
    }

}