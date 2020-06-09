package ru.liqvid.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.tonyodev.fetch2.Status
import io.reactivex.rxjava3.core.Observable
import ru.liqvid.data.remote.api.DownloadFaceNdkService
import ru.liqvid.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class DownloadFaceNdkRepository @Inject constructor(private val downloadFaceNdkService: DownloadFaceNdkService) {

@Singleton
class DownloadFaceNdkRepository () {
    companion object {
        val TAG: String = DownloadFaceNdkRepository::class.java.simpleName
    }

    private var downloadFaceNdkService: DownloadFaceNdkService = DownloadFaceNdkService()
    
    fun loadData(context: Context, l: OnResult) {
        downloadFaceNdkService.fetch3DiViFaceSdk(context).subscribe(
            {
                if (it.status == Status.COMPLETED) {
                    l.success("")
                    Log.i(TAG, "download completed " + it.progress)
                } else if (it.status == Status.DOWNLOADING) {
                    // TODO: тут можно кидать логи на сервер с прогрессом загрузки
                    Log.i(TAG, "download progress " + it.progress)
                }
            },
            {
                l.failure(it.localizedMessage!!)
            }
        )
    }

}

interface OnResult {
    fun success(v: String)
    fun failure(v: String)
}