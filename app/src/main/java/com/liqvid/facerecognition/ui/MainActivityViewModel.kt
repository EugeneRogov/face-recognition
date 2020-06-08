package com.liqvid.facerecognition.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liqvid.facerecognition.Constant
import com.liqvid.facerecognition.util.Util
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func

class MainActivityViewModel : ViewModel() {

    private var downloadFaceNdkStatus = MutableLiveData<String>()

    private var fetch: Fetch? = null

    fun downloadFaceNdkStatus(): LiveData<String> {
        downloadFaceNdkStatus = MutableLiveData()
        return downloadFaceNdkStatus
    }

    fun doDownloadFaceNdkIfNeed(context: Context) {
        if (Util.isExistFaceNdk(Constant.PATH_FACE_REC_CONFIG)) {


            fetchFaceNdk(context)

            downloadFaceNdkStatus.value = "ok"
        } else
            downloadFaceNdkStatus.value = "not ok"
    }

    private fun fetchFaceNdk(context: Context) {
        val fetchConfiguration =
            FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(3)
                .build()
        fetch = Fetch.getInstance(fetchConfiguration)
        val url = Constant.DOWNLOAD_URL
        val file = "/sdcard/face_recognition/test.rar"
        val request = Request(url, file)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG")
        fetch!!.enqueue(
            request,
            Func { updatedRequest: Request? -> },
            Func { error: Error? -> }
        )

        Log.i(MainActivity.TAG, "fetch")

        fetch!!.addListener(object : FetchListener {
            override fun onAdded(download: Download) {
                Log.i(MainActivity.TAG, "onAdded")
            }

            override fun onCancelled(download: Download) {
                Log.i(MainActivity.TAG, "onCancelled")
            }

            override fun onCompleted(download: Download) {
                Log.i(MainActivity.TAG, "onCompleted")
                downloadFaceNdkStatus.value = "Face ndk downloaded"
            }

            override fun onDeleted(download: Download) {
                Log.i(MainActivity.TAG, "onDeleted")
            }

            override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {
                Log.i(MainActivity.TAG, "onDownloadBlockUpdated")
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                Log.i(MainActivity.TAG, "onError")
            }

            override fun onPaused(download: Download) {
                Log.i(MainActivity.TAG, "onPaused")
            }

            override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
                Log.i(MainActivity.TAG, "onProgress")
            }

            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                Log.i(MainActivity.TAG, "onQueued")
            }

            override fun onRemoved(download: Download) {
                Log.i(MainActivity.TAG, "onRemoved")
            }

            override fun onResumed(download: Download) {
                Log.i(MainActivity.TAG, "onResumed")
            }

            override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
                Log.i(MainActivity.TAG, "onStarted")
            }

            override fun onWaitingNetwork(download: Download) {
                Log.i(MainActivity.TAG, "onWaitingNetwork")
            }
        })

    }




}