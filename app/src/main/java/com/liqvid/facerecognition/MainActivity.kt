package com.liqvid.facerecognition

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vdt.face_recognition.sdk.FacerecService
import com.vdt.face_recognition.sdk.SDKException
import java.io.File
import java.util.*

class MainActivity : Activity() {

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
    private var demo: Demo? = null

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

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // if directory with online licence exists then use it otherwise use default offline licence
        val buf = "/sdcard/face_recognition/online_license"
        onlineLicenceDir = if (File(buf).exists()) {
            buf
        } else {
            ""
        }

        // view permissions status
        setContentView(R.layout.permissions)

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
            starting()
        }
    }

    private fun starting() {
        setContentView(R.layout.splash)
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

        Thread(LoadThread(this, service)).start()
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
            starting()
        }
    }

    override fun onResume() {
        super.onResume()
        if (camera != null && demo != null) {
            camera!!.open(demo, cameraId, imWidth, imHeight)
        }
    }

    override fun onPause() {
        if (camera != null) camera!!.close()
        super.onPause()
    }

    override fun onDestroy() {
        if (camera != null)
            camera!!.close()
        if (demo != null)
            demo!!.dispose()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_SETTINGS -> {
                    cameraId = data.getIntExtra("cam_id", cameraId)
                    val stemp = data.getStringExtra("selected_resolution")
                    if (stemp != stringResolution) {
                        demo!!.updateCapturer()
                        setNewResolution(stemp)
                    }
                }
                REQUEST_OPTIONS -> {
                    flags = data.getBooleanArrayExtra("flags")
                    faceCutTypeId = data.getIntExtra("faceCutTypeId", 0)
                    demo!!.setOptions(flags, faceCutTypeId)
                }
            }
        }
    }

    private class LoadThread(var ma: MainActivity, var service: FacerecService) :
        Runnable {
        override fun run() {
            try {
                val camera = TheCamera(ma)
                val demo = Demo(ma, service)
                ma.flags = demo.flags
                ma.faceCutTypeId = demo.faceCutTypeId
                ma.runOnUiThread {
                    ma.demo = demo
                    ma.camera = camera
                    ma.showForm()
                }
            } catch (e: Exception) {
                exceptionHappensDo(ma, e)
                return
            }
        }
    }

    fun showForm() {
        setContentView(R.layout.main_activity)
        val textView = findViewById<View>(R.id.textView) as TextView
        textView.movementMethod = ScrollingMovementMethod()
        demo!!.setTextView()

        val quitButton = findViewById<View>(R.id.quit_button) as Button
        quitButton.setOnClickListener { finishAffinity() }

        val optionsButton = findViewById<View>(R.id.options_button) as Button
        optionsButton.setOnClickListener {
            val toOptionsIntent = Intent(applicationContext, OptionsActivity::class.java)
            toOptionsIntent.putExtra("flags", flags)
            toOptionsIntent.putExtra("faceCutTypeId", faceCutTypeId)
            startActivityForResult(toOptionsIntent, REQUEST_OPTIONS)
        }

        val settingsButton = findViewById<View>(R.id.settings_button) as Button
        settingsButton.setOnClickListener {
            val toSettingsIntent = Intent(applicationContext, SettingsActivity::class.java)
            toSettingsIntent.putExtra("selected_camera_id", cameraId)
            toSettingsIntent.putExtra("selected_resolution", stringResolution)
            startActivityForResult(toSettingsIntent, REQUEST_SETTINGS)
        }

        camera!!.open(demo, cameraId, imWidth, imHeight)
    }

    private fun setNewResolution(resolution: String) {
        val tempStr = resolution.split("x".toRegex()).toTypedArray()
        imWidth = tempStr[0].toInt()
        imHeight = tempStr[1].toInt()
    }

    private val stringResolution: String
        private get() = imWidth.toString() + "x" + imHeight.toString()

}