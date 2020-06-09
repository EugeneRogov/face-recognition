package ru.liqvid.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Observable
import ru.liqvid.data.remote.api.DownloadFaceNdkService
import ru.liqvid.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class DownloadFaceNdkRepository @Inject constructor(private val downloadFaceNdkService: DownloadFaceNdkService) {

@Singleton
class DownloadFaceNdkRepository () {

    private var downloadFaceNdkService: DownloadFaceNdkService = DownloadFaceNdkService()
    
    fun loadData(context: Context, l: OnResult) {
        downloadFaceNdkService.fetch3DiViFaceSdk(context).subscribe(
            { it ->


                l.success("ok")

            },
            {
                l.failure(it.localizedMessage!!)
            }
        )
        l.success("")
    }

}

interface OnResult {
    fun success(v: String)
    fun failure(v: String)
}