package org.song.videoplayer;

/**
 * Created by wangzhiguo on 2017/4/14.
 * 播放器监听 值的意义见IVideoPlayer
 */

public interface PlayListener {
    /**
     * 播放器的ui状态
     */
    void onStatus(int status);

    /**
     * 播放器的ui模式[全屏/普通/...
     */
    void onMode(int mode);

    /**
     * 播放事件
     */
    void onEvent(int what, Integer... extra);
}
