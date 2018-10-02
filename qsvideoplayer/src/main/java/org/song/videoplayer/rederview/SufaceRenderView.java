package org.song.videoplayer.rederview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.song.videoplayer.media.IMediaControl;

/**
 * Created by wangzhiguo on 2017/4/14.
 * 小于4.0用这个绘制view
 * ps:切换全屏效果不好 会停顿
 */
public class SufaceRenderView extends SurfaceView implements SurfaceHolder.Callback, IRenderView {

    protected static final String TAG = "SufaceRenderView";
    private MeasureHelper mMeasureHelper;
    private int videoWidth;
    private int videoHeight;

    private IRenderCallback callback;

    public SufaceRenderView(Context context) {
        super(context);
        init();
    }

    public SufaceRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        //getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        mMeasureHelper = new MeasureHelper(this);
    }


    @Override
    public View get() {
        return this;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth >= 0 && videoHeight >= 0) {
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            if (holder != null)
                holder.setFixedSize(videoWidth, videoHeight);
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);

            requestLayout();
        }
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    public void setVideoRotation(int degree) {
        //不支持
    }

    @Override
    public void addRenderCallback(IRenderCallback callback) {
        this.callback = callback;
    }

    @Override
    public void removeRenderCallback() {
        this.callback = null;
    }


    @Override
    public SurfaceHolder getSurfaceHolder() {
        return holder;
    }

    @Override
    public Surface openSurface() {
        if (holder != null)
            return holder.getSurface();
        return null;
    }

    @Override
    public void bindMedia(IMediaControl iMediaControl) {
        //iMediaControl.setDisplay(null);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        iMediaControl.setDisplay(getSurfaceHolder());
    }

    private SurfaceHolder holder;


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        this.holder = holder;
        if (callback != null)
            callback.onSurfaceCreated(this, 0, 0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged " + width + "-" + height);
        if (callback != null)
            callback.onSurfaceChanged(this, format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        if (callback != null)
            callback.onSurfaceDestroyed(this);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }


}
