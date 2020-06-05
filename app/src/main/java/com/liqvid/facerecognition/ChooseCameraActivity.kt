package com.liqvid.facerecognition

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ChooseCameraActivity : AppCompatActivity(R.layout.choose_camera_activity) {
    companion object {
        val TAG: String = ChooseCameraActivity::class.java.simpleName
    }

    private val rbList = ArrayList<RadioButton?>()
    private var radioGroup: RadioGroup? = null
    private var rb0: RadioButton? = null
    private var rb1: RadioButton? = null
    private var rb2: RadioButton? = null
    private var spinner: Spinner? = null
    private val camsResolutions = ArrayList<ArrayList<String>>()
    private var selectedCameraId = 0
    private var selectedResolution = "640x480"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get previous or default values
        val getValuesIntent = intent
        selectedCameraId = getValuesIntent.getIntExtra("selected_camera_id", selectedCameraId)

        //radio buttons init
        radioGroup = findViewById<View>(R.id.cameras_radio_group) as RadioGroup
        rb0 = findViewById<View>(R.id.camera0_radio_button) as RadioButton
        rb1 = findViewById<View>(R.id.camera1_radio_button) as RadioButton
        rb2 = findViewById<View>(R.id.camera2_radio_button) as RadioButton
        rbList.add(rb0)
        rbList.add(rb1)
        rbList.add(rb2)
        val availableCameras = TheCamera.getAvailableCameras()

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
                for (size in resolutions) {
                    val resolutionString = size.width.toString() + "x" + size.height.toString()
                    camsResolutions[camId].add(resolutionString)
                    Log.d(TAG, "    $resolutionString")
                }
            }
            if (camId == selectedCameraId) {
                isSelectedAvailable = true
            }
        }

        if (!isSelectedAvailable) {
            selectedCameraId = availableCameras[0].id
        }

        val rb = radioGroup!!.getChildAt(selectedCameraId) as RadioButton
        rb.isChecked = true
        spinner = findViewById<View>(R.id.resolution_spinner) as Spinner
        selectedResolution = getValuesIntent.getStringExtra("selected_resolution")
        setSpinnerResolutions(selectedResolution)
        spinner!!.setSelection(camsResolutions[selectedCameraId].indexOf(selectedResolution))

        //ok - button
        val okButton = findViewById<View>(R.id.settings_ok_button) as Button
        okButton.setOnClickListener {
            val setValuesIntent = Intent()
            setValuesIntent.putExtra("cam_id", selectedCameraId)
            setValuesIntent.putExtra("selected_resolution", spinner!!.selectedItem.toString())
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
        spinner!!.adapter = spinnerAdapter
        var spinnerIndex = camsResolutions[selectedCameraId].indexOf(defaultResolution)
        spinnerIndex = if (spinnerIndex == -1) 0 else spinnerIndex
        spinner!!.setSelection(spinnerIndex)
    }

}