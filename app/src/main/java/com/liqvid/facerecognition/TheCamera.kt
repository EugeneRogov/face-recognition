package com.liqvid.facerecognition

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.liqvid.facerecognition.ui.ErrorActivity
import com.vdt.face_recognition.sdk.SDKException
import com.vdt.face_recognition.sdk.utils.Converter_YUV_NV_2_ARGB
import java.io.IOException
import java.util.*

class TheCamera(activity: Activity?) : PreviewCallback {
    companion object {
        val TAG: String = TheCamera::class.java.simpleName

        private const val  MAGIC_TEXTURE_ID = 10

        // no show camera radio button
        val availableCameras: List<TheCameraInfo>
            get() {
                val result: MutableList<TheCameraInfo> = ArrayList()
                val count = Camera.getNumberOfCameras()
                for (i in 0 until count) {
                    var cam: Camera? = null
                    val resolutions: List<Camera.Size>? = null
                    try {
                        cam = Camera.open(i)
                        val params = cam.parameters
                        val camInfo = TheCameraInfo()
                        camInfo.id = i
                        camInfo.resolutions = params.supportedPreviewSizes
                        result.add(camInfo)
                    } catch (e: Exception) {
                        // No show camera radio button
                        Log.e(TAG, "Can't open camera $i")
                        e.printStackTrace()
                    } finally {
                        cam?.release()
                    }
                }
                return result
            }
    }
    private var camera: Camera? = null
    private var activity: Activity? = null
    private var image: ImageView? = null
    private var painter: FaceRecognition? = null
    private var camId = 0
    private var surfaceTexture: SurfaceTexture? = null

    private val bitmap: Bitmap? = null
    private var buf: ByteArray? = null
    private var openFlag = false

    init {
        this.activity = activity
    }

    class TheCameraInfo {
        var id = 0 // id of camera
        var resolutions: List<Camera.Size>? = null
    }

    @Synchronized
    fun open(painter: FaceRecognition?, cam_id: Int, width: Int, height: Int) {
        if (openFlag) return
        this.painter = painter
        openFlag = true
        this.camId = cam_id
        if (camera != null) return
        Log.i(TAG, "Open camera $cam_id")
        try {
//            camera = Camera.open(cam_id)
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
            var camParameters = camera?.parameters
            val fpsRanges = camParameters!!.supportedPreviewFpsRange
            val maxFpsRange = fpsRanges[fpsRanges.size - 1]
            Log.i(TAG, "Set FPS Range (" + maxFpsRange[0] + "," + maxFpsRange[1] + ")")
            camParameters.previewFormat = ImageFormat.NV21
            camParameters.setPreviewSize(width, height)
            camParameters.setPreviewFpsRange(maxFpsRange[0], maxFpsRange[1])
            camera?.parameters = camParameters
            camParameters = camera?.parameters
            val psize = camParameters?.previewSize
            var size = psize!!.width * psize.height
            size = size * ImageFormat.getBitsPerPixel(camParameters!!.previewFormat) / 8
            buf = ByteArray(size)
            surfaceTexture = SurfaceTexture(MAGIC_TEXTURE_ID)
            try {
                camera?.setPreviewTexture(surfaceTexture)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            camera?.addCallbackBuffer(buf)
            camera?.setPreviewCallbackWithBuffer(this)
            camera?.setDisplayOrientation(0)
            camera?.startPreview()
        } catch (e: Exception) {
            Log.e(TAG, "Can't open the camera $cam_id")
            e.printStackTrace()
            close()
            return
        }
    }

    @Synchronized
    fun close() {
        if (!openFlag) return
        openFlag = false
        if (camera == null)
            return

        Log.i(TAG, "TheCamera close")
        camera?.setPreviewCallback(null)
        camera?.stopPreview()
        camera?.release()
//        camera = null
    }

    override fun onPreviewFrame(arg0: ByteArray, arg1: Camera) {
        image = activity?.findViewById<View>(R.id.pv) as ImageView
        val parameters = arg1.parameters
        val size = parameters.previewSize
        val argb = Converter_YUV_NV_2_ARGB.convert_yuv_nv_2_argb(false, arg0, size.width, size.height)
        val immutBitmap = Bitmap.createBitmap(argb, size.width, size.height, Bitmap.Config.ARGB_8888)
        val mutBitmap = immutBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutBitmap)
        try {
            painter!!.processingImage(canvas, arg0, size.width, size.height)
        } catch (e: Exception) {
            val toErrorIntent = Intent(activity, ErrorActivity::class.java)
            toErrorIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (e is SDKException) {
                val sdke = e
                toErrorIntent.putExtra(
                    "error_activity message",
                    """
                        code: ${String.format("0x%08X", sdke.code())}
                        ${sdke.message}
                        """.trimIndent()
                )
            } else {
                toErrorIntent.putExtra("error_activity message", e.message)
            }
            e.printStackTrace()
            activity?.startActivity(toErrorIntent)
            activity?.finish()
            return
        }
        image!!.setImageBitmap(mutBitmap)
        camera?.addCallbackBuffer(buf)
    }

}