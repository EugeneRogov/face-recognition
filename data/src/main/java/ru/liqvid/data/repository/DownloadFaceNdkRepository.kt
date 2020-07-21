package ru.liqvid.data.repository

import android.content.Context
import android.util.Log
import com.tonyodev.fetch2.Status
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.liqvid.data.local.file.UnzipFaceNdkService
import ru.liqvid.data.remote.api.DownloadFaceNdkService
import ru.liqvid.util.Constant
import ru.liqvid.util.FileUtils
import javax.inject.Singleton

@Singleton
class DownloadFaceNdkRepository() {
    companion object {
        val TAG: String = DownloadFaceNdkRepository::class.java.simpleName
    }

    private val downloadFaceNdkService: DownloadFaceNdkService = DownloadFaceNdkService()
    private val unzipFaceNdkService: UnzipFaceNdkService = UnzipFaceNdkService()

    fun loadAndPrepareFaceNdk(context: Context, downloadUrl: String, digest: String, l: OnResult) {
        if (!FileUtils.isExistDirectory(Constant.FR_FOLDER_PATH)) {
            downloadFaceNdkService.fetch3DiViFaceSdk(context, downloadUrl)
                .subscribe(
                    {
                        if (it.status == Status.COMPLETED) {
                            unpack(digest, l)
                            Log.i(TAG, "loadAndPrepareFaceNdk completed " + it.progress)
                        } else if (it.status == Status.DOWNLOADING) {
                            // TODO: логи на сервер с прогрессом загрузки
                            Log.i(TAG, "loadAndPrepareFaceNdk progress " + it.progress)
                        }
                    },
                    {
                        // TODO: логи на сервер об ошибке загрузки
                        Log.i(TAG, "error " + it.localizedMessage!!)
                    }
                )
            Log.i(TAG, "Face ndk downloading...")
        } else {
            l.success(true)
            Log.i(TAG, "Face ndk installed")
        }
    }

    private fun unpack(digest: String, l: OnResult) {
        unzipFaceNdkService.unzip(
            zipFilePath = Constant.FR_ZIP_FILE_PATH,
            destDirectory = Constant.FR_FOLDER_PATH,
            digest = digest
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it) {
                        l.success(true)
                        // TODO: FR lib is ready, can start FR service
                    } else {

                    }

                    Log.i(TAG, "unpack $it")
                },
                {
                    Log.e(TAG, "unpack error $it")
                },
                {
                    Log.i(TAG, "complete")
                }
            )
    }

}

interface OnResult {
    fun success(v: Boolean)
    fun failure(v: String)
}