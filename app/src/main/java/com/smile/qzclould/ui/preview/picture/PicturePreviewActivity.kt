package com.smile.qzclould.ui.preview.picture

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import com.facebook.drawee.drawable.ScalingUtils
import com.gyf.barlibrary.ImmersionBar
import com.smile.qielive.common.BaseActivity
import com.smile.qzclould.R
import com.smile.qzclould.ui.preview.PreviewViewModel
import com.smile.qzclould.uicompment.ImageLoadingDrawable
import kotlinx.android.synthetic.main.activity_picture_preview.*

class PicturePreviewActivity: BaseActivity() {

    private val mModel by lazy { ViewModelProviders.of(this).get(PreviewViewModel::class.java) }
    private lateinit var mPath: String
    private var mIsLocal = false

    override fun setLayoutId(): Int {
        return R.layout.activity_picture_preview
    }

    override fun initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar?.statusBarColor(R.color.color_black_000000)
        mImmersionBar?.fitsSystemWindows(false)
        mImmersionBar?.init()
    }

    override fun initData() {
        mPath = intent.getBundleExtra("bundle_extra").getString("path")
        mIsLocal = intent.getBundleExtra("bundle_extra").getBoolean("isLocal")
        if(!mIsLocal) {
            mModel.getPictureInfo(mPath)
        } else {
            mPhotoView.setPhotoUri(Uri.parse("file://$mPath"))
        }
    }

    override fun initView() {
        mPhotoView.hierarchy?.setProgressBarImage(ImageLoadingDrawable(), ScalingUtils.ScaleType.CENTER_INSIDE)
        mIvBack.setOnClickListener {
            finish()
        }
    }

    override fun initViewModel() {
        mModel.pictureInfoResult.observe(this, Observer {
            mPhotoView.setPhotoUri(Uri.parse(it?.previewHlsAddress))
        })
    }
}