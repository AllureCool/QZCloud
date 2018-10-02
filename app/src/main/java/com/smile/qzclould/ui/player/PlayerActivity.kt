package com.smile.qzclould.ui.player

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.widget.FrameLayout
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.repository.HttpRepository
import com.smile.qzclould.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity: BaseActivity() {

    private val mModel by lazy { ViewModelProviders.of(this).get(MediaViewModel::class.java) }
    private lateinit var mPath: String
    private var mIsLocalVideo: Boolean = false
    private var mFirstLoad: Boolean = true

    override fun setLayoutId(): Int {
        return R.layout.activity_player
    }

    override fun initData() {
        mIsLocalVideo = intent.getBundleExtra("bundle_extra").getBoolean("isLocal")
        mPath = intent.getBundleExtra("bundle_extra").getString("path")
        if(mIsLocalVideo) {
            play(mPath)
        } else {
            mModel.getMediaInfo(mPath)
        }
    }

    override fun initView() {
        mVideoView.layoutParams = FrameLayout.LayoutParams(-1, ViewUtils.getScreenWidth() * 9 / 16)
        mVideoView.enterFullMode = 3
        mVideoView.isWindowGesture = true
        mVideoView.enterWindowFullscreen()

        mVideoView.setOnPlayerControlListener {
            SwitchClarityView().show(supportFragmentManager, "clarity_view")
        }
    }

    override fun initViewModel() {
        mModel.MediaInfoResult.observe(this, Observer {
            if(!it!!.preview.isEmpty()) {
                play(it.preview[0].url + "?token=" + UserInfoManager.get().getUserToken())
            }
        })
    }

    private fun play(url: String) {
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