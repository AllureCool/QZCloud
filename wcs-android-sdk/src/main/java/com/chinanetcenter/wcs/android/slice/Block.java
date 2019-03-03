package com.chinanetcenter.wcs.android.slice;

import android.util.Log;

import com.chinanetcenter.wcs.android.utils.WCSLogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Locale;

public class Block {

    private static final int PER_SLICE_SIZE = 64 * 1024;
    private static final int MAX_BLOCK_SIZE = 100 * 1024 * 1024;
    private static final int DEFAULT_BLOCK_SIZE = 4 * 1024 * 1024;
    private static final int DEFAULT_SLICE_SIZE = 256 * 1024;

    private static int sDefaultSliceSize = DEFAULT_SLICE_SIZE;
    private static long sDefaultBlockSize = DEFAULT_BLOCK_SIZE;

    private RandomAccessFile mRandomAccessFile;
    private long mStart;
    private long mSize;//blocksize 块实际大小，最后一块与其他块大小可能不同
    private int mSliceSize;
    private int mSliceIndex;
    private long mOriginalFileSize;
    private String mFileName;

    public ByteArray getByteArray() {
        return mByteArray;
    }

    public void setByteArray(ByteArray byteArray) {
        mByteArray = byteArray;
    }

    private ByteArray mByteArray;

    Block(RandomAccessFile randomAccessFile, String fileName, long start, long blockSize, int sliceSize) throws IOException {
        mRandomAccessFile = randomAccessFile;
        mOriginalFileSize = randomAccessFile.length();
        mFileName = fileName;
        mStart = start;
        mSize = blockSize;
        mSliceSize = sliceSize;
    }


    public static Block[] blocks(File file) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            Log.e("CNCLog", "file not found : " + file);
            return null;
        }

        long fileSize = 0;
        try {
            fileSize = randomAccessFile.length();
        } catch (IOException e) {
            WCSLogUtil.e(e.getMessage());
        }
        if (fileSize == 0) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
            }
            return null;
        }

        int blockCount = (int) ((fileSize + sDefaultBlockSize - 1) / sDefaultBlockSize);// TODO: 2017/5/3
        WCSLogUtil.d(String.format(Locale.CHINA, "file size : %s, block count : %s", fileSize, blockCount));
        Block[] blocks = new Block[blockCount];
        long blockSize = sDefaultBlockSize;
        for (int i = 0; i < blockCount; i++) {
            if (i + 1 == blockCount) {
                long remain = fileSize % sDefaultBlockSize;
                blockSize = remain == 0 ? sDefaultBlockSize : remain;// TODO: 2017/5/2 最后一块
            }
            try {
                blocks[i] = new Block(randomAccessFile, file.getName(), i * sDefaultBlockSize, blockSize, sDefaultSliceSize);
                //有多少块就创建多少空间
                // TODO: 2017/5/4 需要优化,等真正执行块上传时再创建
//				blocks[i].mByteArrayBuffer = new ByteArrayBuffer(sDefaultSliceSize);// TODO: 2017/5/2 初始化slice
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return blocks;
    }

    public void clear() {
        try {
            mRandomAccessFile.close();
        } catch (IOException e) {
        }
    }

    public void releaseBuffer() {
        if (mByteArray != null) {
            mByteArray.clear();
        }
    }

    public Slice moveToNext() {
        return getSlice(mSliceIndex++);
    }

    public void setIndex(int index) {
        mSliceIndex = index;
    }

    public int getIndex() {
        return mSliceIndex;
    }

    public Slice moveToIndex(int index) {
        mSliceIndex = index;
        return moveToNext();
    }

    public Slice lastSlice() {
        return getSlice(mSliceIndex - 1);
    }

    private Slice getSlice(int index) {
        //如果没有创建buffer，创建之
        if (mByteArray == null) {
            mByteArray = new ByteArray(getSliceSize());
        }
        long offset = mStart + index * getSliceSize();//从1开始
        if (index * getSliceSize() >= mSize) {
            return null;
        }
        int sliceSize = getSliceSize();
        if ((offset + getSliceSize()) > (mStart + mSize)) {//计算最后一片
            sliceSize = (int) (mSize % getSliceSize());
        }
        byte[] sliceData = mByteArray.toBuffer();
        Arrays.fill(sliceData, (byte) 0);
        if (sliceSize < getSliceSize()) {
            sliceData = new byte[sliceSize];
        }

        try {
            mRandomAccessFile.seek(offset);
            mRandomAccessFile.read(sliceData, 0, sliceSize);
            WCSLogUtil.d("offset : " + offset + "; slice size : " + sliceSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sliceSize < getSliceSize()) { // 利用同一个缓存
            return new Slice(index * getSliceSize(), sliceData);//记录最后一片
        } else {
            return new Slice(index * getSliceSize(), mByteArray);
        }
    }

    public long size() {
        return mSize;
    }

    public int getSliceSize() {
        return mSliceSize;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("start", mStart);
            jsonObject.put("size", mSize);
            jsonObject.put("slice index", mSliceIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            return jsonObject.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Block<>";
    }

    public long getOriginalFileSize() {
        return mOriginalFileSize;
    }

    public String getOriginalFileName() {
        return mFileName;
    }

//    public void initFirstSlice() {
//        mByteArrayOutputStream = new ByteArrayOutputStream(sDefaultSliceSize);
//    }

    /**
     * 设置块的大小，默认为4M
     * 注意：块的大小必须是4M的倍数，最大不能超过100M
     *
     * @param size
     */
    public static void setBlockSize(int size) {
        size = size * 1024 * 1024;
        if (size <= 0 || size % sDefaultBlockSize != 0 || size > MAX_BLOCK_SIZE) {
            sDefaultBlockSize = DEFAULT_BLOCK_SIZE;
            return;
        }
        sDefaultBlockSize = size;
        WCSLogUtil.d("block size: " + sDefaultBlockSize);
    }

    /**
     * 设置片的大小，默认为256KB
     * 注意：片的大小必须是64K的倍数，最大不能超过块的大小。
     *
     * @param size
     */
    public static void setSliceSize(int size) {
        size = size * 1024;
        if (size <= 0 || size % PER_SLICE_SIZE != 0 || size > sDefaultBlockSize) {
            sDefaultSliceSize = DEFAULT_SLICE_SIZE;
            return;
        }
        sDefaultSliceSize = size;
        WCSLogUtil.d("slice size: " + sDefaultSliceSize);
    }

}
