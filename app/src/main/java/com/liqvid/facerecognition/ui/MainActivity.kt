package com.liqvid.facerecognition.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.liqvid.facerecognition.FaceRecognition
import com.liqvid.facerecognition.R
import com.liqvid.facerecognition.TheCamera
import com.liqvid.facerecognition.Utils
import com.liqvid.facerecognition.face_recognition.InitService
import com.vdt.face_recognition.sdk.FacerecService
import com.vdt.face_recognition.sdk.SDKException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

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

    private var camera: TheCamera? = null
    private var faceRecognition: FaceRecognition? = null

    // Settings
    private var cameraId = 0
    private var imWidth = 640
    private var imHeight = 480

    // Options
    private lateinit var flags: BooleanArray
    private var faceCutTypeId = 0

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

    private val initService: InitService = InitService()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraId = Utils.getCameraId(baseContext)

        // observe data
        model.downloadFaceNdkStatus().observe(this, Observer { data ->
            if (data)
                starting()
            else
                Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show()
        })


        setContentView(R.layout.main_activity_permissions)

        // check and request main_activity_permissions
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

    private var faceRecService: FacerecService? = null

    private fun starting() {
        setContentView(R.layout.main_activity_splash)

        initService.initFaceRecService(applicationInfo.nativeLibraryDir + "/libfacerec.so")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext ={ frs ->
                    faceRecService = frs
                    Log.i(TAG, "initFaceRecService ok")

                    initService.initFaceRecognition(this, faceRecService!!)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onNext ={ fr ->
                                faceRecognition = fr
                                Log.i(TAG, "initFaceRecognition ok")

                                showForm()
                            },
                            onError = { error ->
                                Log.e(TAG, "initFaceRecognition error ${error.message}")
                            }
                        )
                },
                onError = { error ->
                    Log.e(TAG, "initFaceRecService error ${error.message}")
                }
            )

        initService.initCamera(this)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    camera = it
                    Log.i(TAG, "initCamera ok")

                },
                onError = { Log.e(TAG, "initCamera error ${it.message}")}
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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

//    override fun onResume() {
//        super.onResume()
//        if (camera != null && faceRecognition != null) {
//            camera!!.open(faceRecognition, cameraId, imWidth, imHeight)
//        }
//    }

    override fun onPause() {
        camera?.close()
        super.onPause()
    }

    override fun onDestroy() {
        camera?.close()
        faceRecognition?.dispose()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_SETTINGS -> {
                    cameraId = data!!.getIntExtra("cam_id", cameraId)
                    val stemp = data.getStringExtra("selected_resolution")
                    if (stemp != stringResolution) {
//                        faceRecognition?.updateCapture()
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

    private fun showForm() {
        setContentView(R.layout.main_activity)

        tvData.movementMethod = ScrollingMovementMethod()

        faceRecognition?.setTextView()

        btnStart.setOnClickListener {
            val count = Camera.getNumberOfCameras()
            if (Utils.isCameraAvailable(baseContext)) {
                GlobalScope.launch { camera?.open(faceRecognition!!, cameraId, imWidth, imHeight) }
                Toast.makeText(this, "This device has camera, count of cameras $count", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "This device has no camera", Toast.LENGTH_SHORT).show()
        }

        btnStop.setOnClickListener {
            if (Utils.isCameraAvailable(baseContext)) {
                GlobalScope.launch { camera?.close() }
                Toast.makeText(this, "This device has camera", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "This device has no camera", Toast.LENGTH_SHORT).show()
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

    private fun setNewResolution(resolution: String) {
        val tempStr = resolution.split("x".toRegex()).toTypedArray()
        imWidth = tempStr[0].toInt()
        imHeight = tempStr[1].toInt()
    }

    private val stringResolution: String
        private get() = imWidth.toString() + "x" + imHeight.toString()

}