package ru.liqvid.data.repository

import androidx.lifecycle.LiveData
import ru.liqvid.data.remote.api.DownloadFaceNdkService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadFaceNdkRepository @Inject constructor(private val downloadFaceNdkService: DownloadFaceNdkService) {

//    fun loadData(l: OnResult): LiveData<String> {
//        l.success("")
//    }

}

interface OnResult {

    fun success(v: String)
    fun failure(v: String)
}