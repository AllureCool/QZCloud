package com.chinanetcenter.wcs.android.api;

import com.chinanetcenter.wcs.android.listener.SliceUploaderListener;
import com.chinanetcenter.wcs.android.utils.WCSLogUtil;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public class ProgressNotifier {

    private SliceUploaderListener mUploaderListener;

    private long mTotal;

    //    private long mWritten;
    private AtomicLong mWritten;

    public ProgressNotifier(long total, SliceUploaderListener uploaderListener) {
        mUploaderListener = uploaderListener;
        mTotal = total;
        mWritten = new AtomicLong();
    }

    public void decreaseProgress(long decrease) {
//        mWritten -= decrease;
        mWritten.addAndGet(-decrease);
    }

    public void increaseProgressAndNotify(long written) {
        mWritten.addAndGet(written);
//        mWritten += written;
        if (mWritten.get() <= mTotal) {
            mUploaderListener.onProgress(mWritten.get(), mTotal);
        } else {
            WCSLogUtil.w(String.format(Locale.CHINA, "written (%d) greater than total (%d)", mWritten.get(), mTotal));
            mUploaderListener.onProgress(mTotal, mTotal);
        }
    }

}
