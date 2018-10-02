package org.song.videoplayer;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.song.videoplayer.bean.DefinitionBean;
import org.song.videoplayer.media.IMediaCallback;
import org.song.videoplayer.media.IMediaControl;
import org.song.videoplayer.rederview.IRenderView;
import org.song.videoplayer.rederview.SufaceRenderView;

import java.util.List;

/**
 * Created by wangzhiguo on 2017/4/14.
 * 没有控制ui,纯视频播放器,提供完整控制功能
 */

public class QSVideoView extends FrameLayout implements IVideoPlayer, IMediaCallback {

    public static final String TAG = "QSVideoView";
    public int enterFullMode = 0;//进入全屏的模式 0横屏 1竖屏 2传感器自动横竖屏 3根据视频比例自动确定横竖屏      -1什么都不做

    private IMediaControl iMediaControl;

    protected FrameLayout videoView;
    private FrameLayout renderViewContainer;//suface容器
    private IRenderView iRenderView;

    protected String url;
    protected List<DefinitionBean> definitionList;
    //根据url做进度保存
    protected static ArrayMap<String, Integer> playPosition = new ArrayMap<>();
    protected int currentState = STATE_NORMAL;
    protected int currentMode = MODE_WINDOW_NORMAL;
    protected int currentLockMode = MODE_UNLOCK;
    protected int seekToInAdvance;
    protected int aspectRatio;

    protected PlayListener playListener;
    public int urlMode;
    protected boolean isReleasing;
    protected boolean isWaitPlay;
    //设置同时只需要一个播放器播放,一个开始播放,其他的needSinglePlay为true的播放器会自动release
    public boolean needSinglePlay = true;
    protected static QSVideoView mCurrentPlayer;
    public boolean stayIfUnattach = false;


    public QSVideoView(Context context) {
        this(context, null);
    }

    public QSVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QSVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        iMediaControl = ConfigManage.getInstance(getContext()).getIMediaControl(this);
        videoView = new FrameLayout(context);
        renderViewContainer = new FrameLayout(context);
        renderViewContainer.setBackgroundColor(Color.BLACK);
        videoView.addView(renderViewContainer, new LayoutParams(-1, -1));
        addView(videoView, new LayoutParams(-1, -1));

    }


    //-----------给外部调用的start----------
    @Override
    public void setUp(String url, Object... objects) {
        release();
        setStateAndMode(STATE_NORMAL, currentMode, currentLockMode);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        this.url = url;
        if (url.startsWith("file"))
            urlMode = 1;
    }

    @Override
    public void setDefinitions(List<DefinitionBean> definitions) {
        if(definitions != null) {
            this.definitionList = definitions;
        }
    }

    @Override
    public void play() {
        if (currentState != STATE_PLAYING && currentState != STATE_PREPARING)
            clickPlay();
    }

    @Override
    public void seekTo(int duration) {
        if (checkReady()) {
            if (duration >= 0) {
                seek(duration);
            }
        } else
            seekToInAdvance = duration;
    }

    @Override
    public void pause() {
        if (currentState == STATE_PLAYING)
            clickPlay();
    }

    public boolean isPause() {
        return currentState == STATE_PAUSE;
    }

    public void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    @Override
    public boolean onBackPressed() {
//        if (currentMode != MODE_WINDOW_NORMAL) {
//            quitWindowFullscreen();
//            return true;
//        }
        return false;
    }


    @Override
    public void setAspectRatio(int aspectRatio) {
        if (iRenderView != null)
            iRenderView.setAspectRatio(aspectRatio);
        this.aspectRatio = aspectRatio;
    }

    @Override
    public void setiMediaControl(int i) {
        this.iMediaControl = ConfigManage.getInstance(getContext()).getIMediaControl(this, i);
    }

    @Override
    public boolean isPlaying() {
        return iMediaControl.isPlaying();
    }

    @Override
    public int getPosition() {
        return iMediaControl.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return iMediaControl.getDuration();
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public int getCurrentState() {
        return currentState;
    }

    @Override
    public void release() {
        if (isWaitPlay) {
            isWaitPlay = false;
        }
        if (currentState != STATE_NORMAL && !isReleasing) {
            if (currentMode != STATE_AUTO_COMPLETE) {
                int position = getPosition();
                if (position > 0) {
                    playPosition.put(url, position);
                }
            }
            isReleasing = true;
            new Thread() {
                @Override
                public void run() {
                    try {
                        iMediaControl.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isReleasing = false;
                    //之前暂停的播放队列重启
                    if (isWaitPlay) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                prepareMediaPlayer();
                            }
                        });
                    }
                }
            }.start();
            setStateAndMode(STATE_NORMAL, currentMode, currentLockMode);
            if (needSinglePlay && mCurrentPlayer == this) {
                //mCurrentPlayer = null;
            }
            removeRenderView();
            intiParams();
        }
    }


    private long tempLong;
    private boolean full_flag;//标记状态栏状态
    private boolean orientation_flag;//标记横竖屏状态

    //全屏
    @Override
    public void enterWindowFullscreen() {
        if (currentMode != MODE_WINDOW_FULLSCREEN & checkEnterOrFullOK()) {
            boolean flag = false;
            if (enterFullMode == 3) {
                flag = true;
                enterFullMode = height > width ? 1 : 0;
            }

            full_flag = Util.SET_FULL(getContext());
            orientation_flag = Util.isScreenOriatationPortrait(getContext());

            if (enterFullMode == 0)
                Util.SET_LANDSCAPE(getContext());
            else if (enterFullMode == 1)
                Util.SET_PORTRAIT(getContext());
            else if (enterFullMode == 2)
                Util.SET_SENSOR(getContext());
            if (flag)
                enterFullMode = 3;

            Util.showNavigationBar(getContext(), false);

            ViewGroup vp = (ViewGroup) videoView.getParent();
            if (vp != null)
                vp.removeView(videoView);
            ViewGroup decorView = (ViewGroup) (Util.scanForActivity(getContext())).getWindow().getDecorView();
            //.findViewById(Window.ID_ANDROID_CONTENT);
            decorView.addView(videoView, new LayoutParams(-1, -1));
            setStateAndMode(currentState, MODE_WINDOW_FULLSCREEN, currentLockMode);
        }
    }

    //退出全屏
    @Override
    public void quitWindowFullscreen() {
        if (currentMode != MODE_WINDOW_NORMAL & checkEnterOrFullOK()) {
            if (full_flag)
                Util.SET_FULL(getContext());
            else
                Util.CLEAR_FULL(getContext());
            if (orientation_flag)
                Util.SET_PORTRAIT(getContext());
            else
                Util.SET_LANDSCAPE(getContext());

            Util.showNavigationBar(getContext(), true);

            ViewGroup vp = (ViewGroup) videoView.getParent();
            if (vp != null)
                vp.removeView(videoView);
            addView(videoView, new LayoutParams(-1, -1));
            setStateAndMode(currentState, MODE_WINDOW_NORMAL, currentLockMode);
        }
    }

    //防止频繁切换全屏
    private boolean checkEnterOrFullOK() {
        long now = System.currentTimeMillis();
        long d = now - tempLong;
        if (d > 888)
            tempLong = now;
        return d > 888;
    }

    //-----------给外部调用的end----------


    //-----------解码器回调start-----------------
    @Override
    public void onPrepared(IMediaControl iMediaControl) {
        Log.e("MediaCallBack", "onPrepared");
        if (seekToInAdvance > 0) {
            iMediaControl.seekTo(seekToInAdvance);
            seekToInAdvance = 0;
        }
        iMediaControl.doPlay();
        setStateAndMode(STATE_PLAYING, currentMode, currentLockMode);
        if (playListener != null)
            playListener.onEvent(EVENT_PREPARE_END, 0);
    }


    @Override
    public void onCompletion(IMediaControl iMediaControl) {
        Log.e("MediaCallBack", "onCompletion");
        playPosition.put(url, 0);
        setStateAndMode(STATE_AUTO_COMPLETE, currentMode, currentLockMode);
        if (playListener != null)
            playListener.onEvent(EVENT_COMPLETION);
    }

    @Override
    public void onSeekComplete(IMediaControl iMediaControl) {
        Log.e("MediaCallBack", "onSeekComplete");
    }

    @Override
    public void onInfo(IMediaControl iMediaControl, int what, int extra) {
        Log.e("MediaCallBack", "onInfo" + " what" + what + " extra" + extra);
        if (what == IMediaControl.MEDIA_INFO_BUFFERING_START) {
            onBuffering(true);
            if (playListener != null)
                playListener.onEvent(EVENT_BUFFERING_START);
        }

        if (what == IMediaControl.MEDIA_INFO_BUFFERING_END) {
            onBuffering(false);
            if (playListener != null)
                playListener.onEvent(EVENT_BUFFERING_END);
        }
    }

    private int width, height;

    @Override
    public void onVideoSizeChanged(IMediaControl iMediaControl, int width, int height) {
        Log.e("MediaCallBack", "onVideoSizeChanged" + " width:" + width + " height:" + height);
        iRenderView.setVideoSize(width, height);
        this.width = width;
        this.height = height;
        if (playListener != null)
            playListener.onEvent(EVENT_VIDEOSIZECHANGE, width, height);
    }

    @Override
    public void onError(IMediaControl iMediaControl, int what, int extra) {
        Log.e("MediaCallBack", "onError" + "what:" + what + " extra:" + extra);
        //if (what == 38 | extra == -38 | extra == -19)
        //    return;
        Toast.makeText(getContext(), "播放失败,错误码: " + what + "," + extra, Toast.LENGTH_SHORT).show();
        seekToInAdvance = getPosition();//记录错误时进度
        iMediaControl.release();
        setStateAndMode(STATE_ERROR, currentMode, currentLockMode);
        if (playListener != null)
            playListener.onEvent(EVENT_ERROR, what, extra);
    }

    @Override
    public void onBufferingUpdate(IMediaControl iMediaControl, float bufferProgress) {
        Log.e("MediaCallBack", "onBufferingUpdate" + bufferProgress);
        setBufferProgress(bufferProgress);
        if (playListener != null)
            playListener.onEvent(EVENT_BUFFERING_UPDATE, (int) (bufferProgress * 100));
    }

    //给子类覆盖
    protected void onBuffering(boolean isBuffering) {
    }

    //给子类覆盖 0~progressMax
    protected void setBufferProgress(float bufferProgress) {
    }

    //-----------解码器回调end-----------------


    //-----------各种流程逻辑start-----------------

    //初始化一些变量
    protected void intiParams() {
        this.width = 0;
        this.height = 0;
    }

    //设置播放状态
    private void setStateAndMode(final int status, final int mode, final int lockMode) {
        if (Looper.getMainLooper() == Looper.myLooper())
            setUIWithStateAndMode(status, mode, lockMode);
        else
            post(new Runnable() {
                @Override
                public void run() {
                    setUIWithStateAndMode(status, mode, lockMode);
                }
            });
    }

    protected void setUIWithStateAndMode(final int status, final int mode, final int lockMode) {
        Log.e("setStateAndMode", "status:" + status + " mode:" + mode);
        if (status == STATE_PLAYING)
            Util.KEEP_SCREEN_ON(getContext());
        else
            Util.KEEP_SCREEN_OFF(getContext());

        final int temp_status = this.currentState;
        final int temp_mode = this.currentMode;
        this.currentState = status;
        this.currentMode = mode;
        this.currentLockMode = lockMode;
        if (playListener != null) {
            if (temp_status != status)
                playListener.onStatus(status);
            if (temp_mode != mode)
                playListener.onMode(mode);
        }
    }

    //点击时根据不同状态做出不同的反应
    protected void clickPlay() {
        if (needSinglePlay) {
            if (mCurrentPlayer != null && mCurrentPlayer != this) {
                //if (mCurrentPlayer.currentState == STATE_PREPARING) {
                releaseCurrentPlayer();
                //} else {
                //    mCurrentPlayer.pause();
                //}
            }
            setCurrentVideoPlayer(this);
        }
        if (currentState == STATE_NORMAL) {
            if (urlMode != 1) {
                if (!Util.isConnected(getContext())) {
                    Toast.makeText(getContext(), "网络连接不可用", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!Util.isWifiConnected(getContext())) {
                        if (showWifiDialog())
                            return;
                    }
                }
            }
            prepareMediaPlayer();
        } else if (currentState == STATE_PLAYING) {
            if (isReleasing) {
                return;
            }
            iMediaControl.doPause();
            setStateAndMode(STATE_PAUSE, currentMode, currentLockMode);
        } else if (currentState == STATE_PAUSE) {
            if (isReleasing) {
                return;
            }
            iMediaControl.doPlay();
            setStateAndMode(STATE_PLAYING, currentMode, currentLockMode);
        } else if (currentState == STATE_AUTO_COMPLETE || currentState == STATE_ERROR) {
            if (!Util.isConnected(getContext())) {
                Toast.makeText(getContext(), "网络连接不可用", Toast.LENGTH_SHORT).show();
            } else {
                prepareMediaPlayer();
            }
        }
    }

    //锁定屏幕点击处理
    protected void clickLock() {
        if(currentLockMode == MODE_LOCKED) {
            setStateAndMode(currentState, currentMode, MODE_UNLOCK);
        } else {
            setStateAndMode(currentState, currentMode, MODE_LOCKED);
        }
    }

    public boolean showWifiDialog() {
        return false;
    }

    //一开始点击准备播放--初始化
    protected void prepareMediaPlayer() {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isReleasing) {
            isWaitPlay = true;
            return;
        }
        isWaitPlay = false;
        Log.d(TAG, "prepareMediaPlayer [" + this.hashCode() + "] ");
        removeRenderView();
        iMediaControl.doPrepar(getContext(), url, null);
        addRenderView();
        if (currentState != STATE_PREPARING) {
            setStateAndMode(STATE_PREPARING, currentMode, currentLockMode);
        }
        if (playPosition.get(url) != null) {
            seekTo(playPosition.get(url));
        }
        if (playListener != null)
            playListener.onEvent(EVENT_PREPARE_START);
    }

    private void addRenderView() {
        iRenderView = ConfigManage.getInstance(getContext()).getIRenderView(getContext());
        iRenderView.addRenderCallback(new IRenderView.IRenderCallback() {
            @Override
            public void onSurfaceCreated(IRenderView holder, int width, int height) {
                holder.bindMedia(iMediaControl);
            }

            @Override
            public void onSurfaceChanged(IRenderView holder, int format, int width, int height) {

            }

            @Override
            public void onSurfaceDestroyed(IRenderView holder) {
                if (holder instanceof SufaceRenderView) {
                    iMediaControl.setDisplay(null);
                }
            }
        });
        iRenderView.setAspectRatio(aspectRatio);
        LayoutParams layoutParams = new LayoutParams(-1, -1, Gravity.CENTER);
        renderViewContainer.addView(iRenderView.get(), layoutParams);
    }

    private void removeRenderView() {
        renderViewContainer.removeAllViews();
        if (iRenderView != null) {
            iRenderView.removeRenderCallback();
            iRenderView = null;
        }
    }


    private void seek(int time) {
        if (currentState == STATE_PLAYING ||
                currentState == STATE_PAUSE)
            iMediaControl.seekTo(time);
        if (currentState == STATE_AUTO_COMPLETE) {
            //seekToInAdvance = time;//播放完成 拖动进度条重新播放
            //prepareMediaPlayer();
            iMediaControl.seekTo(time);
            iMediaControl.doPlay();
            setStateAndMode(STATE_PLAYING, currentMode, currentLockMode);
        }
    }


    protected boolean checkReady() {
        return currentState != STATE_NORMAL
                & currentState != STATE_PREPARING
                & currentState != STATE_ERROR;
    }

    public String getUrl() {
        return url;
    }

    public int getVideoWidth() {
        return width;
    }

    public int getVideoHeight() {
        return height;
    }

    public void setCurrentVideoPlayer(QSVideoView player) {
        mCurrentPlayer = player;
    }

    public static QSVideoView getCurrentVideoPlayer() {
        return mCurrentPlayer;
    }

    public static void releaseCurrentPlayer() {
        if (mCurrentPlayer != null) {
            mCurrentPlayer.release();
            mCurrentPlayer = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!stayIfUnattach && currentMode == MODE_WINDOW_NORMAL) {
            release();
        }
    }

}
