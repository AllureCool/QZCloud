package com.smile.qzclould.event

const val EVENT_CANCEl = 0
const val EVENT_SELECTALL = 1
const val EVENT_DELETE = 2
const val EVENT_DOWNLOAD = 3


class FileControlEvent {

    var controlCode: Int = 0

    constructor(controlCode: Int) {
        this.controlCode = controlCode
    }
}