package com.liqvid.facerecognition.face_recognition

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.liqvid.facerecognition.R
import com.vdt.face_recognition.sdk.*
import com.vdt.face_recognition.sdk.RawSample.FaceCutType
import com.vdt.face_recognition.sdk.VideoWorker.TrackingCallback
import com.vdt.face_recognition.sdk.utils.Converter_YUV_NV_2_ARGB
import java.lang.Boolean
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class FaceRecognition(private val activity: Activity, service: FacerecService) :
    TheCameraPainter {

    companion object {
        val TAG: String = FaceRecognition::class.java.simpleName
    }

    private var textView: TextView? = null
    private var faceRecService: FacerecService = service

    private var videoWorker: VideoWorker = service.createVideoWorker(
        VideoWorker.Params()
            .video_worker_config(
                service.Config("video_worker_fdatracker.xml")
                    .overrideParameter("search_k", 10.0)
                    .overrideParameter("downscale_rawsamples_to_preferred_size", 0.0)
            )
            .recognizer_ini_file(Const.VideoWorker.MethodRecognizer.METHOD_9_V30)
            .streams_count(1)
            .processing_threads_count(1)
            .matching_threads_count(1)
            .age_gender_estimation_threads_count(1)
            // .emotions_estimation_threads_count(1)
            .short_time_identification_enabled(true)
            .short_time_identification_distance_threshold(Const.VideoWorker.THRESHOLD.VALUE)
            .short_time_identification_outdate_time_seconds(5f)
    )

    private var flagRectangle = true
    private var flagAngles = true
    private var flagQuality = false
    private var flagLiveness = false
    private var flagAgeAndGender = false
    private var flagPoints = true
    private var flagFaceQuality = false
    private var flagAnglesVectors = true
    private var flagEmotions = false
    private val id2le = HashMap<Int, LivenessEstimator>()
    private var faceCutType: FaceCutType? = null

    private var streamId = 0

    init {
        //add callbacks
        videoWorker.addTrackingCallbackU(TrackingCallbacker())
    }


    private class TrackingCallbacker : TrackingCallback {
        override fun call(data: TrackingCallbackData) {
            if (data.stream_id != 0) return

                for (i in data.samples.indices) {
                    val sample = data.samples[i]
                    val id = sample.id
//                    if (!drawingData.faces.containsKey(id)) {
//                        val faceData = FaceData(sample)
//                        drawingData.faces.put(id, faceData)
//                    }
//                    val face: FaceData = drawingData.faces.get(id)
//                    face.frame_id = sample.frameID
//                    face.lost = false
//                    face.weak = data.samples_weak[i]
//                    face.sample = sample


                    // just print age gender and emotions
                    Log.i(TAG, "  age gender set: " + Boolean.toString(data.samples_track_age_gender_set[i]))
                    Log.i(TAG, "  emotions set: " + Boolean.toString(data.samples_track_emotions_set[i]))
                    if (data.samples_track_age_gender_set[i]) {
                        Log.i(TAG, "  age:       " + data.samples_track_age_gender[i].age.name)
                        Log.i(TAG, "  gender:    " + data.samples_track_age_gender[i].gender.name)
                        Log.i(TAG, "  age_years: " + java.lang.Float.toString(data.samples_track_age_gender[i].age_years))
                    }
                    if (data.samples_track_emotions_set[i]) {
                        for (j in data.samples_track_emotions[i].indices) Log.i(
                            TAG, "  emotion: " + data.samples_track_emotions[i][j].emotion.name + " confidence: " + java.lang.Float.toString(data.samples_track_emotions[i][j].confidence)
                        )
                    }
                }
//            }
        }
    }

    fun setTextView() {
        textView = activity.findViewById<View>(R.id.tvData) as TextView
    }

    fun setOptions(flags: BooleanArray?, faceCutTypeId: Int) {
        if (flags != null) {
            flagRectangle = flags[0]
            flagAngles = flags[1]
            flagQuality = flags[2]
            flagLiveness = flags[3]
            flagAgeAndGender = flags[4]
            flagPoints = flags[5]
            flagFaceQuality = flags[6]
            flagAnglesVectors = flags[7]
            flagEmotions = flags[8]
        }
        faceCutType =
            if (faceCutTypeId > 0) FaceCutType.values()[faceCutTypeId - 1] else null
    }

    val flags: BooleanArray
        get() = booleanArrayOf(
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

    val faceCutTypeId: Int
        get() = if (faceCutType != null) faceCutType!!.ordinal else 0

    fun dispose() {
        faceRecService.dispose()
        videoWorker.dispose()
    }

    private fun setWeight(id: Int, weight: Float) {
        val view = activity.findViewById<View>(id)
        val p = view.layoutParams as LinearLayout.LayoutParams
        p.weight = weight
        view.layoutParams = p
    }

    private val frames = LinkedBlockingQueue<Pair<Int, Bitmap>>()

    private val stream_id = 0

    override fun processingImage(data: ByteArray?, width: Int, height: Int) {
        // get RawImage
        val frame = RawImage(width, height, RawImage.Format.FORMAT_YUV_NV21, data)
        val argb = Converter_YUV_NV_2_ARGB.convert_yuv_nv_2_argb(false, data, width, height)

        val immut_bitmap = Bitmap.createBitmap(argb, width, height, Bitmap.Config.ARGB_8888)
//        val mut_bitmap = immut_bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val frame_id = videoWorker.addVideoFrame(frame, stream_id)
//        frames.offer(Pair(frame_id, mut_bitmap))
    }

}