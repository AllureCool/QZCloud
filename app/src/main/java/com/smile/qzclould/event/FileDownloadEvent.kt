package com.smile.qzclould.event

class FileDownloadEvent {

    var shouldDownloadNow: Boolean = false

    constructor(shouldDownloadNow: Boolean) {
        this.shouldDownloadNow = shouldDownloadNow
    }
}