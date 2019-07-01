package com.smile.qzclould.repository.requestbody

data class OfflineAddBody(
        val path: String,
        val task: List<OfflineTask>
) {
     class OfflineTask {
        var identity: String? = null
        var iginreFiles: List<String>? = null
    }
}