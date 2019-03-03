package com.chinanetcenter.wcs.android.slice;


public class ByteArray {
    private byte[] buffer;

    public ByteArray(int capacity) {

        buffer = new byte[capacity];
    }

    public byte[] toBuffer() {
        return buffer == null ? new byte[0] : buffer;
    }

    public void clear() {
        buffer = null;
    }

    public int size() {
        return buffer == null ? 0 : buffer.length;
    }
}
