package com.chinanetcenter.wcs.android.slice;

public class Slice {

    public static final int SLICE_MAX_RETRY = 2;
    private byte[] mData;
    private long mOffset;
    private ByteArray mByteArray;

    /**
     * 上传完会校验正确性，如果不正确则需要重试
     * 重试次数
     */
    private int mRetry;

    Slice(long offset, ByteArray byteArray) {
        this.mOffset = offset;
        this.mByteArray = byteArray;
    }

    Slice(long offset, byte[] data) {
        mOffset = offset;
        mData = data;
    }

    public long size() {
        if (mByteArray != null) {
            return mByteArray.size();
        } else if (mData != null) {
            return mData.length;
        }
        return 0;
    }

    public byte[] toByteArray() {
        if (null != mByteArray) {
            return mByteArray.toBuffer();
        } else if (null != mData) {
            return mData;
        }
        return new byte[0];
    }

    public long getOffset() {
        return mOffset;
    }

    public int getRetry() {
        return mRetry;
    }

    public void setRetry(int retry) {
        this.mRetry = retry;
    }
}
