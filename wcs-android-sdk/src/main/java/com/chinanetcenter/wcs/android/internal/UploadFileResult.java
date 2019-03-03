package com.chinanetcenter.wcs.android.internal;

import com.chinanetcenter.wcs.android.network.WcsResult;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.internal
 * @class : ${CLASS_NAME}
 * @time : 2017/5/11 ${ITME}
 * @description :TODO
 */
public class UploadFileResult extends WcsResult {

    // Object的ETag值。
    private String eTag;

    // 设置server回调的请求，会返回回调server的响应内容
    private String serverCallbackReturnBody;

    /**
     * 返回新创建的Object的ETag值。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * @param eTag 新创建的Object的ETag值。
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * 如果设置了serverCallback，上传后会返回回调结果
     *
     * @return 回调结果的json串
     */
    public String getServerCallbackReturnBody() {
        return serverCallbackReturnBody;
    }

    public void setServerCallbackReturnBody(String serverCallbackReturnBody) {
        this.serverCallbackReturnBody = serverCallbackReturnBody;
    }
}
