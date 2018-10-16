package com.smile.qzclould.event

class SelectDownloadPathEvent(
        val path: String,
        val opt: Int = 0,
        val eventId: Int
)