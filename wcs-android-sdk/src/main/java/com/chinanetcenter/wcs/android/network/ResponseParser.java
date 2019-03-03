package com.chinanetcenter.wcs.android.network;

import okhttp3.Response;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.network
 * @class : ${CLASS_NAME}
 * @time : 2017/5/10 ${ITME}
 * @description :TODO
 */
public interface ResponseParser<T> {
    public T parse(Response response) throws Exception;
}
