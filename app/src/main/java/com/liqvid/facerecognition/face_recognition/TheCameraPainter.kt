package com.liqvid.facerecognition.face_recognition

interface TheCameraPainter {
    fun processingImage(data: ByteArray?, width: Int, height: Int)
}