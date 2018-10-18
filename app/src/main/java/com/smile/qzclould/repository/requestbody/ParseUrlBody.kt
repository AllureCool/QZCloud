package com.smile.qzclould.repository.requestbody

import java.io.Serializable

/**
 * 预解析文件请求body
 */
data class ParseUrlBody(
     val url: String  //要下载的Url
): Serializable