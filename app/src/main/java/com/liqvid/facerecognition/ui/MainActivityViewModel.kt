package com.liqvid.facerecognition.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private var downloadFaceNdkStatus = MutableLiveData<String>()

    fun downloadFaceNdkStatus(): LiveData<String> {
        downloadFaceNdkStatus = MutableLiveData()
        return downloadFaceNdkStatus
    }

    fun doDownloadFaceNdk() {
        downloadFaceNdkStatus.value = "ok"
    }
}