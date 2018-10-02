package com.smile.qzclould.ui.player

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.text.TextUtils
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.manager.UserInfoManager
import kotlinx.android.synthetic.main.activity_audio_player.*
import org.song.videoplayer.QSAudioManager

class AudioPlayerActivity : BaseActivity() {

    private val mModel by lazy { ViewModelProviders.of(this).get(MediaViewModel::class.java) }
    private lateinit var mPath: String
    private var mIsLocalVideo: Boolean = false
    private var mFirstLoad: Boolean = true
    private val audioManager by lazy { QSAudioManager(this) }

    override fun setLayoutId(): Int {
        return R.layout.activity_audio_player
    }

    override fun initData() {
        mIsLocalVideo = intent.getBundleExtra("bundle_extra").getBoolean("isLocal")
        mPath = intent.getBundleExtra("bundle_extra").getString("path")
        if (mIsLocalVideo) {
            play(mPath)
        } else {
            mModel.getMediaInfo(mPath)
        }
    }

    override fun initView() {
        play.setOnClickListener({
            if (!TextUtils.isEmpty(audioManager.url)) {
                if (audioManager.isPlaying) {
                    audioManager.pause()
                } else {
                    audioManager.play()
                }
            }
        })
    }

    override fun initViewModel() {
        mModel.MediaInfoResult.observe(this, Observer {
            if (!it!!.preview.isEmpty()) {
                play(it.preview[0].url + "?token=" + UserInfoManager.get().getUserToken())
            }
        })
    }

    private fun play(url: String) {
        audioManager.setUp(url, "")
        audioManager.play()
    }

    override fun onResume() {
        super.onResume()
        if (!mFirstLoad) {
            audioManager.play()
        }
        mFirstLoad = false
    }

    override fun onStop() {
        super.onStop()
        audioManager.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.release()
    }

}