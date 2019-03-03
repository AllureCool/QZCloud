package com.chinanetcenter.wcs.android.entity;

import java.util.ArrayList;

/**
 * block 分片
 * 片上传
 * POST /bput/<ctx>/<nextChunkOffset>
 * Host: <UploadDomain>
 * Authorization:<UploadToken>
 * Content-Length:<ChunkSize>
 * Content-Type:application/octet-stream
 * UploadBatch:<uuid>
 * Key:<key>
 * <p>
 * <ChunkBinary>
 */
public class SliceCache {

    public String uploadBatch;
    private String fileHash;//key
    /**
     * {
     * "ctx":          "<Ctx           string>",
     * "checksum":     "<Checksum      string>",
     * "crc32":         "<Crc32         int64>",
     * "offset":        "<Offset        int64>"
     * }
     */
    private ArrayList<String> blockContext;
    private ArrayList<Integer> blockUploadedIndex;

    private long sliceSize;
    private long blockSize;

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public long getBlockSize() {
        return this.blockSize;
    }

    public void setSliceSize(long sliceSize) {
        this.sliceSize = sliceSize;
    }

    public long getSliceSize() {
        return this.sliceSize;
    }


    public String getUploadBatch() {
        if (null == uploadBatch) {
            return "";
        }
        return uploadBatch;
    }

    public void setUploadBatch(String uploadBatch) {
        this.uploadBatch = uploadBatch;
    }

    public String getFileHash() {
        if (null == fileHash) {
            return "";
        }
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public ArrayList<String> getBlockContext() {
        if (null == blockContext) {
            return new ArrayList<String>();
        }
        return blockContext;
    }

    public void setBlockContext(ArrayList<String> blockContext) {
        this.blockContext = blockContext;
    }

    public ArrayList<Integer> getBlockUploadedIndex() {
        if (null == blockUploadedIndex) {
            return new ArrayList<Integer>();
        }
        return blockUploadedIndex;
    }

    public void setBlockUploadedIndex(ArrayList<Integer> blockUploadedIndex) {
        this.blockUploadedIndex = blockUploadedIndex;
    }

    @Override
    public String toString() {
        String cacheString = fileHash;
        cacheString += "; context ";
        for (String context : getBlockContext()) {
            cacheString += ("\t" + context);
        }
        cacheString += ";";
        cacheString += "; index ";
        for (Integer index : getBlockUploadedIndex()) {
            cacheString += ("\t" + index);
        }
        cacheString += ";";
        return cacheString;
    }

}
