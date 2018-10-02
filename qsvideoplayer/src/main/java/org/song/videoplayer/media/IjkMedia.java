package org.song.videoplayer.media;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by wangzhiguo on 2017/4/14.
 * 哔哩哔哩播放器
 */

public class IjkMedia extends IjkBaseMedia {

    public IjkMedia(IMediaCallback iMediaCallback) {
        super(iMediaCallback);
    }

    @Override
    IMediaPlayer getMedia(Context context, String url, Map<String, String> headers) throws Exception {
        IjkMediaPlayer mediaPlayer = new IjkMediaPlayer();

        if (url.startsWith(ContentResolver.SCHEME_CONTENT) || url.startsWith(ContentResolver.SCHEME_ANDROID_RESOURCE))
            mediaPlayer.setDataSource(context, Uri.parse(url), headers);
        else
            mediaPlayer.setDataSource(url, headers);
        return mediaPlayer;
    }
}
