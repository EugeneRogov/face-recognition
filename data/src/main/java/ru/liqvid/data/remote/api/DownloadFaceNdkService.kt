package ru.liqvid.data.remote.api

import android.content.Context
import android.util.Log
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock

import com.tonyodev.fetch2core.Func

class DownloadFaceNdkService {
    fun fetch3DiViFaceSdk(context: Context) {

        val fetchConfiguration =
            FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(3)
                .build()

        val fetch = Fetch.getInstance(fetchConfiguration)
//        val url = Constant.DOWNLOAD_URL
//        val file = Constant.SD_CARD_FACE_RECOGNITION

        val url = "Constant.DOWNLOAD_URL"
        val file = "Constant.SD_CARD_FACE_RECOGNITION"

        val request = Request(url, file)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG")
        fetch.enqueue(
            request,
            Func { updatedRequest: Request? -> },
            Func { error: Error? -> }
        )

        Log.i("MainActivity.TAG", "fetch")

        fetch.addListener(object : FetchListener {
            override fun onAdded(download: Download) {

            }

            override fun onCancelled(download: Download) {

            }

            override fun onCompleted(download: Download) {
                Log.i("DownloadFaceNdkService", "onCompleted")

            }

            override fun onDeleted(download: Download) {
                Log.i("DownloadFaceNdkService", "onDeleted")
            }

            override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {
                Log.i("DownloadFaceNdkService", "onDownloadBlockUpdated")
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                Log.i("DownloadFaceNdkService", "onError")
            }

            override fun onPaused(download: Download) {
                Log.i("DownloadFaceNdkService", "onPaused")
            }

            override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
                Log.i("DownloadFaceNdkService", "onProgress")
            }

            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                Log.i("DownloadFaceNdkService", "onQueued")
            }

            override fun onRemoved(download: Download) {
                Log.i("DownloadFaceNdkService", "onRemoved")
            }

            override fun onResumed(download: Download) {
                Log.i("DownloadFaceNdkService", "onResumed")
            }

            override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
                Log.i("DownloadFaceNdkService", "onStarted")
            }

            override fun onWaitingNetwork(download: Download) {
                Log.i("DownloadFaceNdkService", "onWaitingNetwork")
            }
        })

    }
}