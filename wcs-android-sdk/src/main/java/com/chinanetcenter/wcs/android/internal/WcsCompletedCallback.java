package com.chinanetcenter.wcs.android.internal;

import com.chinanetcenter.wcs.android.entity.OperationMessage;
import com.chinanetcenter.wcs.android.network.WcsResult;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.internal
 * @class : ${CLASS_NAME}
 * @time : 2017/5/11 ${ITME}
 * @description :TODO
 */
public interface WcsCompletedCallback<T1, T2 extends WcsResult> {
    public void onSuccess(T1 request, T2 result);

    public void onFailure(T1 request, OperationMessage operationMessage);
//    public void onFailure(T1 request, Exception e);
}
