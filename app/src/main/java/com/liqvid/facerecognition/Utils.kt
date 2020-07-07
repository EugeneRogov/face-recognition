package com.liqvid.facerecognition

import android.content.Context
import android.content.pm.PackageManager


object Utils {

    fun isCameraAvailable(context: Context): Boolean {
        val packageManager: PackageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }



}