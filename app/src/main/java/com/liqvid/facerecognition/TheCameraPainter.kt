package com.liqvid.facerecognition

interface TheCameraPainter {
    fun processingImage(data: ByteArray?, width: Int, height: Int)
}