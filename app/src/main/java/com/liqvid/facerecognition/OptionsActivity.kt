package com.liqvid.facerecognition

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup

class OptionsActivity : Activity() {
    var radioGroup: RadioGroup? = null
    private var flag_rectangle = false
    private var flag_angles = false
    private var flag_quality = false
    private var flag_liveness = false
    private var flag_age_and_gender = false
    private var flag_points = false
    private var flag_face_quality = false
    private var flag_angles_vectors = false
    private var flag_emotions = false
    private var faceCutTypeId = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.options_activity)
        val cb_rectangle = findViewById<View>(R.id.rectangle) as CheckBox
        val cb_angles = findViewById<View>(R.id.angles) as CheckBox
        val cb_quality = findViewById<View>(R.id.quality) as CheckBox
        val cb_liveness = findViewById<View>(R.id.liveness) as CheckBox
        val cb_age_and_gender =
            findViewById<View>(R.id.age_and_gender) as CheckBox
        val cb_points = findViewById<View>(R.id.points) as CheckBox
        val cb_face_quality =
            findViewById<View>(R.id.face_quality) as CheckBox
        val cb_angles_vectors =
            findViewById<View>(R.id.angles_vectors) as CheckBox
        val cb_emotions = findViewById<View>(R.id.emotions) as CheckBox
        val getFlagsIntent = intent
        val flags = getFlagsIntent.getBooleanArrayExtra("flags")
        faceCutTypeId = getFlagsIntent.getIntExtra("faceCutTypeId", 0)
        radioGroup = findViewById<View>(R.id.face_cut_radio_group) as RadioGroup
        val rb = radioGroup!!.getChildAt(faceCutTypeId) as RadioButton
        rb.isChecked = true
        cb_rectangle.isChecked = flags[0]
        cb_angles.isChecked = flags[1]
        cb_quality.isChecked = flags[2]
        cb_liveness.isChecked = flags[3]
        cb_age_and_gender.isChecked = flags[4]
        cb_points.isChecked = flags[5]
        cb_face_quality.isChecked = flags[6]
        cb_angles_vectors.isChecked = flags[7]
        cb_emotions.isChecked = flags[8]
        flag_rectangle = flags[0]
        flag_angles = flags[1]
        flag_quality = flags[2]
        flag_liveness = flags[3]
        flag_age_and_gender = flags[4]
        flag_points = flags[5]
        flag_face_quality = flags[6]
        flag_angles_vectors = flags[7]
        flag_emotions = flags[8]

        //ok - button
        val okButton =
            findViewById<View>(R.id.options_ok_button) as Button
        okButton.setOnClickListener {
            val setValuesIntent = Intent()
            val flags = booleanArrayOf(
                flag_rectangle,
                flag_angles,
                flag_quality,
                flag_liveness,
                flag_age_and_gender,
                flag_points,
                flag_face_quality,
                flag_angles_vectors,
                flag_emotions
            )
            setValuesIntent.putExtra("flags", flags)
            val cur_rb =
                findViewById<View>(radioGroup!!.checkedRadioButtonId) as RadioButton
            when (cur_rb.id) {
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
            R.id.rectangle -> flag_rectangle = if (checked) true else false
            R.id.angles -> flag_angles = if (checked) true else false
            R.id.quality -> flag_quality = if (checked) true else false
            R.id.liveness -> flag_liveness = if (checked) true else false
            R.id.age_and_gender -> flag_age_and_gender = if (checked) true else false
            R.id.points -> flag_points = if (checked) true else false
            R.id.face_quality -> flag_face_quality = if (checked) true else false
            R.id.angles_vectors -> flag_angles_vectors = if (checked) true else false
            R.id.emotions -> flag_emotions = if (checked) true else false
        }
    }
}