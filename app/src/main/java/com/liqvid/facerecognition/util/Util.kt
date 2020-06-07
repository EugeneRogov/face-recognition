package com.liqvid.facerecognition.util

import java.io.File

object Util {

    fun isExistFaceNdk(dirPath: String): Boolean {
        var ret = false
        val dir = File(dirPath)
        if (dir.exists() && dir.isDirectory)
            ret = true
        return ret
    }

}