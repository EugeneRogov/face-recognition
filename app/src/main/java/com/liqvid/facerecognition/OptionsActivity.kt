package com.liqvid.facerecognition

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.options_activity.*

class OptionsActivity : AppCompatActivity(R.layout.options_activity) {

    private var flagRectangle = false
    private var flagAngles = false
    private var flagQuality = false
    private var flagLiveness = false
    private var flagAgeAndGender = false
    private var flagPoints = false
    private var flagFaceQuality = false
    private var flagAnglesVectors = false
    private var flagEmotions = false
    private var faceCutTypeId = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val getFlagsIntent = intent
        val flags = getFlagsIntent.getBooleanArrayExtra("flags")
        faceCutTypeId = getFlagsIntent.getIntExtra("faceCutTypeId", 0)
        val rb = face_cut_radio_group!!.getChildAt(faceCutTypeId) as RadioButton
        rb.isChecked = true

        rectangle.isChecked = flags[0]
        angles.isChecked = flags[1]
        quality.isChecked = flags[2]
        liveness.isChecked = flags[3]

        age_and_gender.isChecked = flags[4]
        points.isChecked = flags[5]
        face_quality.isChecked = flags[6]
        angles_vectors.isChecked = flags[7]
        emotions.isChecked = flags[8]

        flagRectangle = flags[0]
        flagAngles = flags[1]
        flagQuality = flags[2]
        flagLiveness = flags[3]
        flagAgeAndGender = flags[4]
        flagPoints = flags[5]
        flagFaceQuality = flags[6]
        flagAnglesVectors = flags[7]
        flagEmotions = flags[8]

        //ok - button
        val okButton =
            findViewById<View>(R.id.options_ok_button) as Button
        okButton.setOnClickListener {
            val setValuesIntent = Intent()
            val flags = booleanArrayOf(
                flagRectangle,
                flagAngles,
                flagQuality,
                flagLiveness,
                flagAgeAndGender,
                flagPoints,
                flagFaceQuality,
                flagAnglesVectors,
                flagEmotions
            )
            setValuesIntent.putExtra("flags", flags)
            val curRb =
                findViewById<View>(face_cut_radio_group!!.checkedRadioButtonId) as RadioButton
            when (curRb.id) {
                R.id.none_radio_button -> faceCutTypeId = 0
                R.id.base_cut_radio_button -> faceCutTypeId = 1
                R.id.full_cut_radio_button -> faceCutTypeId = 2
                R.id.token_cut_radio_button -> faceCutTypeId = 3
            }
            setValuesIntent.putExtra("faceCutTypeId", faceCutTypeId)
            setResult(RESULT_OK, setValuesIntent)
            finish()
        }
    }

    fun onCheckboxClicked(view: View) {
        val checked = (view as CheckBox).isChecked
        when (view.getId()) {
            R.id.rectangle -> flagRectangle = checked
            R.id.angles -> flagAngles = checked
            R.id.quality -> flagQuality = checked
            R.id.liveness -> flagLiveness = checked
            R.id.age_and_gender -> flagAgeAndGender = checked
            R.id.points -> flagPoints = checked
            R.id.face_quality -> flagFaceQuality = checked
            R.id.angles_vectors -> flagAnglesVectors = checked
            R.id.emotions -> flagEmotions = checked
        }
    }
}