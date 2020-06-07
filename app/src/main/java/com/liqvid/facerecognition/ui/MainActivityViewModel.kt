package com.liqvid.facerecognition.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liqvid.facerecognition.Constant
import com.liqvid.facerecognition.util.Util

class MainActivityViewModel : ViewModel() {

    private var downloadFaceNdkStatus = MutableLiveData<String>()

    fun downloadFaceNdkStatus(): LiveData<String> {
        downloadFaceNdkStatus = MutableLiveData()
        return downloadFaceNdkStatus
    }

    fun doDownloadFaceNdk() {
        if (Util.isExistFaceNdk(Constant.PATH_FACE_REC_CONFIG)) {
            downloadFaceNdkStatus.value = "ok"
        } else
            downloadFaceNdkStatus.value = "not ok"
    }
}