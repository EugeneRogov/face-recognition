package com.liqvid.facerecognition.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.liqvid.facerecognition.FaceRecognition
import com.liqvid.facerecognition.R
import com.liqvid.facerecognition.toBitmap
import com.liqvid.facerecognition.toByteArray
import com.vdt.face_recognition.sdk.FacerecService
import com.vdt.face_recognition.sdk.SDKException
import com.vdt.face_recognition.sdk.utils.Converter_YUV_NV_2_ARGB
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_SETTINGS = 1
        private const val REQUEST_OPTIONS = 2

        val TAG: String = MainActivity::class.java.simpleName

        private fun exceptionHappensDo(activity: Activity, e: Exception) {
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
            activity.startActivity(toErrorIntent)
            activity.finish()
        }
    }

    //    private var camera: TheCamera? = null
    private var camera: Camera? = null
    private var faceRecognition: FaceRecognition? = null

    // Settings
    private var cameraId = 1
    private var imWidth = 640
    private var imHeight = 480

    // Options
    private lateinit var flags: BooleanArray
    private var faceCutTypeId = 0
    var onlineLicenceDir: String? = null

    private val permissionsStr = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    )
    private val permissionsTvId = intArrayOf(
            R.id.camera_perm_status,
            R.id.read_storage_perm_status,
            R.id.write_storage_perm_status,
            R.id.read_phone_perm_status
    )

    private val model: MainActivityViewModel by viewModels()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // observe data
        model.downloadFaceNdkStatus().observe(this, Observer { data ->
            if (data)
                starting()
            else
                Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show()
        })


        // if directory with online licence exists then use it otherwise use default offline licence
        val buf = "/sdcard/face_recognition/online_license"
        onlineLicenceDir = if (File(buf).exists()) {
            buf
        } else {
            ""
        }

        setContentView(R.layout.main_activity_permissions)

        // check and request permissions
        var grantedCount = 0
        for (i in permissionsStr.indices) {
            val perstr = permissionsStr[i]
            if (ContextCompat.checkSelfPermission(this, perstr) == PackageManager.PERMISSION_GRANTED) {
                val tv = findViewById<View>(permissionsTvId[i]) as TextView
                tv.text = " granted "
                tv.setTextColor(Color.GREEN)
                ++grantedCount
            }
        }

        if (grantedCount < permissionsStr.size) {
            ActivityCompat.requestPermissions(this, permissionsStr, 0)
        } else {
            model.doDownloadFaceNdkIfNeed()
        }
    }

    private fun starting() {
        setContentView(R.layout.main_activity_splash)

        val service = FacerecService.createService(
                applicationInfo.nativeLibraryDir + "/libfacerec.so",
                "/sdcard/face_recognition/conf/facerec",
                onlineLicenceDir
        )
        val licenseState = service.licenseState

        Log.i(TAG, "license_state.online            = " + licenseState.online)
        Log.i(TAG, "license_state.android_app_id    = " + licenseState.android_app_id)
        Log.i(TAG, "license_state.ios_app_id        = " + licenseState.ios_app_id)
        Log.i(TAG, "license_state.hardware_reg      = " + licenseState.hardware_reg)

//        Thread(LoadThread(this, service)).start()


        showForm(service)
    }

    private fun showForm(facerecService: FacerecService?) {
        setContentView(R.layout.main_activity)

        startCamera(facerecService)

        tvData.movementMethod = ScrollingMovementMethod()

        faceRecognition?.setTextView()



        btnStart.setOnClickListener {
//            camera?.open(faceRecognition, cameraId, imWidth, imHeight)
        }

        btnStop.setOnClickListener {
//            camera?.close()
        }

        btnChooseCamera.setOnClickListener {
            val toSettingsIntent = Intent(applicationContext, ChooseCameraActivity::class.java)
            toSettingsIntent.putExtra(ChooseCameraActivity.EXTRA_SELECTED_CAMERA_ID, cameraId)
            toSettingsIntent.putExtra("selected_resolution", stringResolution)
            startActivityForResult(
                    toSettingsIntent,
                    REQUEST_SETTINGS
            )
        }

        btnOptions.setOnClickListener {
            val toOptionsIntent = Intent(applicationContext, OptionsActivity::class.java)
            toOptionsIntent.putExtra("flags", flags)
            toOptionsIntent.putExtra("faceCutTypeId", faceCutTypeId)
            startActivityForResult(
                    toOptionsIntent,
                    REQUEST_OPTIONS
            )
        }

        btnQuit.setOnClickListener { finishAffinity() }
    }

//    private class LoadThread(var ma: MainActivity, var service: FacerecService) :
//        Runnable {
//        override fun run() {
//            try {
//                val camera = TheCamera(ma)
//                val demo = FaceRecognition(ma, service)
//                ma.flags = demo.flags
//                ma.faceCutTypeId = demo.faceCutTypeId
//                ma.runOnUiThread {
//                    ma.faceRecognition = demo
//                    ma.camera = camera
//                    ma.showForm()
//                }
//            } catch (e: Exception) {
//                exceptionHappensDo(
//                    ma,
//                    e
//                )
//                return
//            }
//        }
//    }

//    fun decodeBitmap(image: ImageProxy): Bitmap? {
//        val buffer = image.planes[0].buffer
//        val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//    }


    class MyTestAnalyzer(private val fr: FaceRecognition) : ImageAnalysis.Analyzer {
        private var imWidth = 640
        private var imHeight = 480
        override fun analyze(image: ImageProxy) {
            Log.i(TAG, "### Would analyze the image here ..." + image.height)



            Log.i(TAG, "### size ..." + image.toBitmap().toByteArray())


            // Fatal signal 11 (SIGSEGV), code 1, fault addr 0x98712000 in tid 24865 (pool-1-thread-1)
//            val argb = Converter_YUV_NV_2_ARGB.convert_yuv_nv_2_argb(false, image.toBitmap().toByteArray(), imWidth, imHeight)
//            val immutBitmap = Bitmap.createBitmap(argb, imWidth, imHeight, Bitmap.Config.ARGB_8888)
//            val mutBitmap = immutBitmap.copy(Bitmap.Config.ARGB_8888, true)
//            val canvas = Canvas(mutBitmap)
//            fr.processingImage(Canvas(), toByteArray(image), imWidth, imHeight)
            image.close()
        }



//        private fun imageToBitmap(image: ImageProxy): ByteArray {
//            val bitmap = (image.image as BitmapDrawable).bitmap
//            val stream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
//
//            return stream.toByteArray()
//        }

//        private fun toByteArray(image: ImageProxy): ByteArray? {
//            val buffer = image.planes[0].buffer
//            return ByteArray(buffer.capacity()).also { buffer.get(it) }
//        }
//
//        fun decodeBitmap(image: ImageProxy): Bitmap? {
//            val buffer = image.planes[0].buffer
//            val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
//            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//        }
    }

    private val executor = Executors.newSingleThreadExecutor()

    private fun startCamera(facerecService: FacerecService?) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        val fr = FaceRecognition(this, facerecService!!)


        cameraProviderFuture.addListener(Runnable {
            // used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


            // preview
            val preview = Preview.Builder()
                    .setTargetResolution(Size(imWidth, imHeight))
                    .build()


//            val imageAnalysis = ImageAnalysis.Analyzer {
//                val argb = Converter_YUV_NV_2_ARGB.convert_yuv_nv_2_argb(false, decodeBitmap(it), imWidth, imHeight)
//                val immutBitmap = Bitmap.createBitmap(argb, imWidth, imHeight, Bitmap.Config.ARGB_8888)
//                val mutBitmap = immutBitmap.copy(Bitmap.Config.ARGB_8888, true)
//                val canvas = Canvas(mutBitmap)
//                fr.processingImage(canvas, decodeBitmap(it), imWidth, imHeight)
//                it.close()
//            }

            val analyzer = ImageAnalysis.Builder().build()
            val analyzerUseCase = analyzer.apply {
                setAnalyzer(executor, MyTestAnalyzer(fr))
            }


            // select front camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

            try {
                // unbind use cases before rebinding
                cameraProvider.unbindAll()

                // bind use cases to camera
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, analyzerUseCase, preview)


                preview.setSurfaceProvider(pv.createSurfaceProvider())


//                val argb = Converter_YUV_NV_2_ARGB.convert_yuv_nv_2_argb(false, arg0, imWidth, imHeight)
//                val immutBitmap = Bitmap.createBitmap(argb, imWidth, imHeight, Bitmap.Config.ARGB_8888)
//                val mutBitmap = immutBitmap.copy(Bitmap.Config.ARGB_8888, true)
//                val canvas = Canvas(mutBitmap)
//
//                fr.processingImage(canvas, arg0, imWidth, imHeight)


            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var askAgain = false
        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                val tv = findViewById<View>(permissionsTvId[i]) as TextView
                tv.text = " granted "
                tv.setTextColor(Color.GREEN)
            }
            // READ_PHONE_STATE is optional
            else if (permissions[i] != Manifest.permission.READ_PHONE_STATE) {
                askAgain = true
            }
        }
        if (askAgain) {
            val activity: Activity = this
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    ActivityCompat.requestPermissions(activity, permissionsStr, 0)
                }
            }, 2000)
        } else {
            model.doDownloadFaceNdkIfNeed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_SETTINGS -> {
                    cameraId = data!!.getIntExtra("cam_id", cameraId)
                    val stemp = data.getStringExtra("selected_resolution")
                    if (stemp != stringResolution) {
                        faceRecognition?.updateCapture()
                        setNewResolution(stemp)
                    }
                }
                REQUEST_OPTIONS -> {
                    flags = data!!.getBooleanArrayExtra("flags")
                    faceCutTypeId = data.getIntExtra("faceCutTypeId", 0)
                    faceRecognition!!.setOptions(flags, faceCutTypeId)
                }
            }
        }
    }


    private fun setNewResolution(resolution: String) {
        val tempStr = resolution.split("x".toRegex()).toTypedArray()
        imWidth = tempStr[0].toInt()
        imHeight = tempStr[1].toInt()
    }

    private val stringResolution: String
        private get() = imWidth.toString() + "x" + imHeight.toString()


}