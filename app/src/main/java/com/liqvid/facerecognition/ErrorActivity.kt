package com.liqvid.facerecognition

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle

class ErrorActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.error_activity)

        val getValuesIntent = intent
        val message = getValuesIntent.getStringExtra("error_activity message")

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.error)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(R.string.close) { dialog, id ->
                dialog.cancel()
                finishAffinity()
            }
        val alert = builder.create()
        alert.show()
    }

}