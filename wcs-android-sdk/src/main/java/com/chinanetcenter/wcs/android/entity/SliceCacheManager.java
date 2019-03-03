package com.chinanetcenter.wcs.android.entity;

import com.chinanetcenter.wcs.android.utils.WCSLogUtil;

import java.util.HashSet;

public class SliceCacheManager {

    private static SliceCacheManager sInstance;
    private static HashSet<SliceCache> sCacheSet;

    private SliceCacheManager() {
        sCacheSet = new HashSet<SliceCache>();
    }

    public static synchronized SliceCacheManager getInstance() {
        if (null == sInstance) {
            sInstance = new SliceCacheManager();
        }
        return sInstance;
    }

    public synchronized void addSliceCache(SliceCache sliceCache) {
        if (null == sliceCache) {
            return;
        }
        if (null == sliceCache.getFileHash() || sliceCache.getFileHash().length() <= 0) {
            return;
        }
        if (sliceCache.getBlockContext().size() <= 0) {
            return;
        }
        if (sliceCache.getBlockUploadedIndex().size() <= 0) {
            return;
        }

        if (sliceCache.getBlockContext().size() != sliceCache.getBlockUploadedIndex().size()) {
            return;
        }
        for (SliceCache cache : sCacheSet) {
            if (cache.getFileHash().equals(sliceCache.getFileHash())) {
                //已存在则替换
                sCacheSet.remove(cache);
            }
        }
        sCacheSet.add(sliceCache);
    }

    public synchronized SliceCache getSliceCache(String fileHash) {
        if (null != fileHash && fileHash.length() >= 0) {
            for (SliceCache sliceCache : sCacheSet) {
                if (sliceCache.getFileHash().equals(fileHash)) {
                    return sliceCache;
                }
            }
        }
        return null;
    }

    public synchronized void removeSliceCache(SliceCache sliceCache) {
        if (null != sliceCache) {
            sCacheSet.remove(sliceCache);
        }
    }

    public synchronized void dumpAll() {
        for (SliceCache cache : sCacheSet) {
            WCSLogUtil.i("cache : " + cache);
        }
    }

}
