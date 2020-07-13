package com.liqvid.facerecognition

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera

object Utils {

    fun isCameraAvailable(context: Context): Boolean {
        val packageManager: PackageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    fun getCameraId(context: Context): Int {
        return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
            Camera.CameraInfo.CAMERA_FACING_FRONT
        else
            Camera.CameraInfo.CAMERA_FACING_BACK
    }

}