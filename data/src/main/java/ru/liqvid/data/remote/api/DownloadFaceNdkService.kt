package ru.liqvid.data.remote.api

import android.content.Context
import android.util.Log
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock

import com.tonyodev.fetch2core.Func
import io.reactivex.rxjava3.core.Observable
import ru.liqvid.util.Constant

class DownloadFaceNdkService {
    companion object {
        val TAG: String = DownloadFaceNdkService::class.java.simpleName
    }

    /**
     * Download zip file specified by the downloadUrl
     *
     * Sends download progress
     *
     * @param context
     * @param downloadUrl
     */
    fun fetch3DiViFaceSdk(context: Context, downloadUrl: String): Observable<Download> {
        return Observable.create { subscriber ->
            val fetchConfiguration =
                FetchConfiguration.Builder(context)
                    .setDownloadConcurrentLimit(3)
                    .build()

            val fetch = Fetch.getInstance(fetchConfiguration)

            val request = Request(downloadUrl, Constant.FR_ZIP_FILE_PATH)
            request.priority = Priority.HIGH
            request.networkType = NetworkType.ALL
            fetch.enqueue(
                request,
                Func { updatedRequest: Request? -> },
                Func { error: Error? -> }
            )

            fetch.addListener(object : FetchListener {
                override fun onAdded(download: Download) {

                }

                override fun onCancelled(download: Download) {

                }

                override fun onCompleted(download: Download) {
                    subscriber.onNext(download)
                    Log.i(TAG, "onCompleted")
                }

                override fun onDeleted(download: Download) {
                    Log.i(TAG, "onDeleted")
                }

                override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {
                    Log.i(TAG, "onDownloadBlockUpdated " + download.progress)
                }

                override fun onError(download: Download, error: Error, throwable: Throwable?) {
                    Log.i(TAG, "onError")
                }

                override fun onPaused(download: Download) {
                    Log.i(TAG, "onPaused")
                }

                override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
                    subscriber.onNext(download)
                }

                override fun onQueued(download: Download, waitingOnNetwork: Boolean) {

                    Log.i(TAG, "onQueued")
                }

                override fun onRemoved(download: Download) {
                    Log.i(TAG, "onRemoved")
                }

                override fun onResumed(download: Download) {
                    Log.i(TAG, "onResumed")
                }

                override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
                    Log.i(TAG, "onStarted")
                }

                override fun onWaitingNetwork(download: Download) {
                    Log.i(TAG, "onWaitingNetwork")
                }
            })

        }

    }
}