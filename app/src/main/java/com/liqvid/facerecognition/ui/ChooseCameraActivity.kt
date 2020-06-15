package com.liqvid.facerecognition.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.liqvid.facerecognition.R
import com.liqvid.facerecognition.TheCamera
import kotlinx.android.synthetic.main.choose_camera_activity.*
import java.util.*

class ChooseCameraActivity : AppCompatActivity(R.layout.choose_camera_activity) {
    companion object {
        val TAG: String = ChooseCameraActivity::class.java.simpleName
        const val EXTRA_SELECTED_CAMERA_ID = "selected_camera_id"
    }

    private val rbList = ArrayList<RadioButton?>()

    private val camsResolutions = ArrayList<ArrayList<String>>()
    private var selectedCameraId = 0
    private var selectedResolution = "640x480"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get previous or default values
        selectedCameraId = intent.getIntExtra(EXTRA_SELECTED_CAMERA_ID, selectedCameraId)

        rbList.add(camera0_radio_button)
        rbList.add(camera1_radio_button)
        rbList.add(camera2_radio_button)
        val availableCameras = TheCamera.availableCameras

        if (availableCameras.size == 0) {

            Log.e(TAG, "No available cameras")

            val toErrorIntent = Intent(this, ErrorActivity::class.java)
            toErrorIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            toErrorIntent.putExtra("error_activity message", "No available cameras")
            startActivity(toErrorIntent)
            finish()
            return
        }

        for (i in rbList.indices) {
            camsResolutions.add(ArrayList())
            rbList[i]!!.visibility = View.GONE
        }

        var isSelectedAvailable = false
        for (i in availableCameras.indices) {
            val camId = availableCameras[i].id
            val resolutions = availableCameras[i].resolutions

            Log.d(TAG, "Camera $camId available. Resolutions:")

            if (camId < rbList.size) {
                rbList[camId]!!.visibility = View.VISIBLE
                for (size in resolutions!!) {
                    val resolutionString = size.width.toString() + "x" + size.height.toString()
                    camsResolutions[camId].add(resolutionString)
                    Log.d(TAG, "    $resolutionString")
                }
            }
            if (camId == selectedCameraId) {
                isSelectedAvailable = true
            }
        }

        if (!isSelectedAvailable)
            selectedCameraId = availableCameras[0].id

        val rb = rgCameras?.getChildAt(selectedCameraId) as RadioButton
        rb.isChecked = true

        selectedResolution = intent.getStringExtra("selected_resolution")
        setSpinnerResolutions(selectedResolution)
        spinnerResolution?.setSelection(
            camsResolutions[selectedCameraId].indexOf(
                selectedResolution
            )
        )

        // set listeners
        btnOk.setOnClickListener {
            val setValuesIntent = Intent()
            setValuesIntent.putExtra("cam_id", selectedCameraId)
            setValuesIntent.putExtra("selected_resolution", spinnerResolution?.selectedItem.toString())
            setResult(RESULT_OK, setValuesIntent)
            finish()
        }
    }

    fun onRadioClicked(view: View) {
        when (view.id) {
            R.id.camera0_radio_button -> {
                selectedCameraId = 0
                setSpinnerResolutions("640x480")
            }
            R.id.camera1_radio_button -> {
                selectedCameraId = 1
                setSpinnerResolutions("640x480")
            }
            R.id.camera2_radio_button -> {
                selectedCameraId = 2
                setSpinnerResolutions("640x480")
            }
        }
    }

    private fun setSpinnerResolutions(defaultResolution: String) {
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            camsResolutions[selectedCameraId].toTypedArray()
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerResolution?.adapter = spinnerAdapter
        var spinnerIndex = camsResolutions[selectedCameraId].indexOf(defaultResolution)
        spinnerIndex = if (spinnerIndex == -1) 0 else spinnerIndex
        spinnerResolution?.setSelection(spinnerIndex)
    }

}