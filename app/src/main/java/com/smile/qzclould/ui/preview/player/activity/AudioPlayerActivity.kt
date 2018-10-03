package com.smile.qzclould.ui.preview.player.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.text.TextUtils
import com.gyf.barlibrary.ImmersionBar
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.manager.UserInfoManager
import com.smile.qzclould.ui.preview.PreviewViewModel
import kotlinx.android.synthetic.main.activity_audio_player.*
import org.song.videoplayer.PlayListener
import org.song.videoplayer.QSAudioManager

class AudioPlayerActivity : BaseActivity() {

    private val mModel by lazy { ViewModelProviders.of(this).get(PreviewViewModel::class.java) }
    private lateinit var mPath: String
    private var mIsLocalVideo: Boolean = false
    private var mFirstLoad: Boolean = true
    private lateinit var mAudioName: String
    private val audioManager by lazy { QSAudioManager(this) }

    override fun setLayoutId(): Int {
        return R.layout.activity_audio_player
    }

    override fun initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar?.statusBarDarkFont(true, 0.2f)
        mImmersionBar?.statusBarColor(R.color.color_white_ffffff)
        mImmersionBar?.fitsSystemWindows(false)
        mImmersionBar?.init()
    }

    override fun initData() {
        mIsLocalVideo = intent.getBundleExtra("bundle_extra").getBoolean("isLocal")
        mPath = intent.getBundleExtra("bundle_extra").getString("path")
        mAudioName = intent.getBundleExtra("bundle_extra").getString("audio_name")
        if (mIsLocalVideo) {
            play(mPath)
        } else {
            mModel.getMediaInfo(mPath)
        }
    }

    override fun initView() {
        mAudioTitle.text = mAudioName
        mIvBack.setOnClickListener {
            finish()
        }
        play.setOnClickListener({
            if (!TextUtils.isEmpty(audioManager.url)) {
                if (audioManager.isPlaying) {
                    audioManager.pause()
                    play.setImageDrawable(resources.getDrawable(R.mipmap.audio_play))
                } else {
                    audioManager.play()
                    play.setImageDrawable(resources.getDrawable(R.mipmap.audio_pause))
                }
            }
        })
        audioManager.setPlayListener(object : PlayListener {

            override fun onMode(mode: Int) {

            }

            override fun onStatus(status: Int) {

            }
            override fun onEvent(what: Int, vararg extra: Int?) {
                if(what == QSAudioManager.EVENT_COMPLETION) {
                    play.setImageDrawable(resources.getDrawable(R.mipmap.audio_play))
                } else if(what == QSAudioManager.EVENT_PREPARE_START) {
                    play.setImageDrawable(resources.getDrawable(R.mipmap.audio_pause))
                }
            }
        })
    }

    override fun initViewModel() {
        mModel.MediaInfoResult.observe(this, Observer {
            if (!it!!.preview.isEmpty()) {
                play.setImageDrawable(resources.getDrawable(R.mipmap.audio_pause))
                play(it.preview[0].url + "?token=" + UserInfoManager.get().getUserToken())
            }
        })
    }

    private fun play(url: String) {
        audioManager.setUp(url, "")
        audioManager.play()
        play.setImageDrawable(resources.getDrawable(R.mipmap.audio_pause))
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