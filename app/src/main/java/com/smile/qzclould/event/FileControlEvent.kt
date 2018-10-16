package com.smile.qzclould.event

const val EVENT_CANCEl = 0
const val EVENT_SELECTALL = 1
const val EVENT_DELETE = 2
const val EVENT_DOWNLOAD = 3
const val EVENT_MOVE = 4
const val EVENT_COPY = 5

class FileControlEvent {

    var controlCode: Int = 0
    var eventId: Int = 0
    constructor(controlCode: Int, eventId: Int) {
        this.controlCode = controlCode
        this.eventId = eventId
    }
}