package org.song.videoplayer.bean;

import java.io.Serializable;

public class DefinitionBean implements Serializable {
    private String mDefinition;

    private String mVideoStream;

    public String getDefinition() {
        return mDefinition;
    }

    public void setDefinition(String definition) {
        mDefinition = definition;
    }

    public String getVideoStream() {
        return mVideoStream;
    }

    public void setVideoStream(String videoStream) {
        mVideoStream = videoStream;
    }
}
