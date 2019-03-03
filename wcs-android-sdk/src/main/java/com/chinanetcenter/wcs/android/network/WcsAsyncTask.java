package com.chinanetcenter.wcs.android.network;

import java.util.concurrent.Future;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.network
 * @class : ${CLASS_NAME}
 * @time : 2017/5/11 ${ITME}
 * @description :TODO
 */
public class WcsAsyncTask<T extends WcsResult> {

    private Future<T> future;

    private ExecutionContext context;

    private volatile boolean canceled;

    /**
     * 取消任务
     */
    public void cancel() {
        canceled = true;
        if (context != null) {
            context.getCancellationHandler().cancel();
        }
    }

    /**
     * 检查任务是否已经完成
     *
     * @return
     */
    public boolean isCompleted() {
        return future.isDone();
    }

    /**
     * 阻塞等待任务完成，并获取结果,用于同步操作
     *
     * @return
     */
    public T getResult() throws Exception {
        T result = future.get();
        return result;
    }

    public static WcsAsyncTask wrapRequestTask(Future future, ExecutionContext context) {
        WcsAsyncTask asynTask = new WcsAsyncTask();
        asynTask.future = future;
        asynTask.context = context;
        return asynTask;
    }

    /**
     * 阻塞等待任务完成
     */
    public void waitUntilFinished() {
        try {
            future.get();
        } catch (Exception ignore) {
        }
    }

    /**
     * 任务是否已经被取消过
     */
    public boolean isCanceled() {
        return canceled;
    }
}
