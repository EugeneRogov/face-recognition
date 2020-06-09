package com.liqvid.facerecognition.util

import java.io.File

object Util {

    fun isExistDirectory(dirPath: String): Boolean {
        var ret = false
        val dir = File(dirPath)
        if (dir.exists() && dir.isDirectory)
            ret = true
        return ret
    }

    fun isExistFile(dirPath: String): Boolean {
        var ret = false
        val dir = File(dirPath)
        if (dir.exists())
            ret = true
        return ret
    }

}