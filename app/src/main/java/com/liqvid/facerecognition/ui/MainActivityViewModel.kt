package com.liqvid.facerecognition.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.liqvid.data.repository.DownloadFaceNdkRepository
import ru.liqvid.data.repository.OnResult
import ru.liqvid.util.Constant

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG: String = MainActivityViewModel::class.java.simpleName
    }

    private var downloadFaceNdkRepository: DownloadFaceNdkRepository = DownloadFaceNdkRepository()

    private var downloadFaceNdkStatus = MutableLiveData<Boolean>()
    private var context = application.baseContext

    fun downloadFaceNdkStatus(): LiveData<Boolean> {
        downloadFaceNdkStatus = MutableLiveData()
        return downloadFaceNdkStatus
    }

    fun doDownloadFaceNdkIfNeed() {
        downloadFaceNdkRepository.loadAndPrepareFaceNdk(
            context,
            Constant.FR_DOWNLOAD_URL,
            "951e87423c2bce28fd59cdf6258dc6f1",
            object : OnResult {
                override fun success(v: Boolean) {
                    downloadFaceNdkStatus.value = v
                }

                override fun failure(v: String) {
                    TODO("Not yet implemented")
                }

            })
    }

}