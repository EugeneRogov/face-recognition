package com.liqvid.facerecognition.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.liqvid.facerecognition.util.Util
import com.tonyodev.fetch2.Fetch
import ru.liqvid.data.di.Constant
import ru.liqvid.data.repository.DownloadFaceNdkRepository
import ru.liqvid.data.repository.OnResult

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var downloadFaceNdkRepository: DownloadFaceNdkRepository = DownloadFaceNdkRepository()

    private var downloadFaceNdkStatus = MutableLiveData<String>()

    private var fetch: Fetch? = null

    private var context = application.baseContext

    fun downloadFaceNdkStatus(): LiveData<String> {
        downloadFaceNdkStatus = MutableLiveData()
        return downloadFaceNdkStatus
    }

    fun doDownloadFaceNdkIfNeed() {
        if (Util.isExistFaceNdk("/sdcard/face_recognition")) {

            downloadFaceNdkStatus.value = "ok"
        } else {
            downloadFaceNdkStatus.value = "Ndk not exist"
            downloadFaceNdkRepository.loadData(context, object : OnResult{
                override fun success(v: String) {
                    downloadFaceNdkStatus.value = "Face ndk downloaded"
                }

                override fun failure(v: String) {
                    TODO("Not yet implemented")
                }
            })

        }
    }

}