package com.liqvid.facerecognition

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.vdt.face_recognition.sdk.*
import com.vdt.face_recognition.sdk.RawSample.FaceCutType
import com.vdt.face_recognition.sdk.VideoWorker.TrackingCallback
import com.vdt.face_recognition.sdk.utils.Converter_YUV_NV_2_ARGB
import java.lang.Boolean
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class FaceRecognition(private val activity: Activity, service: FacerecService) : TheCameraPainter {

    companion object {
        val TAG: String = FaceRecognition::class.java.simpleName

        private const val STREAMS_COUNT: Int = 2
        private const val PROCESSING_THREADS_COUNT: Int = 2
        private const val MATCHING_THREADS_COUNT: Int = 1


    }

    private var textView: TextView? = null
    private var faceRecService: FacerecService = service

    private var videoWorker: VideoWorker

    //    private var qualityEstimator: QualityEstimator? = null
//    private var ageGenderEstimator: AgeGenderEstimator? = null
//    private var emotionsEstimator: EmotionsEstimator? = null
//    private var faceQualityEstimator: FaceQualityEstimator? = null
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

//    private val stream_id = 0

    private var streamId = 0

    init {

        //create videoWorker
        videoWorker = service.createVideoWorker(
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

        //add callbacks

        //add callbacks
        videoWorker.addTrackingCallbackU(TrackingCallbacker())
    }


    private class TrackingCallbacker : TrackingCallback {
        override fun call(data: TrackingCallbackData) {
            if (data.stream_id != 0) return

            //get frame
//            val frame: Bitmap
//            while (true) {
//                if (frames.size == 0) {
//                    return
//                }
//                if (frames.peek().first == data.frame_id) {
//                    frame = frames.poll().second
//                    break
//                } else {
//                    Log.v(
//                        com.vdt.face_recognition.video_recognition_demo.VidRecDemo.TAG,
//                        "Skiped " + Integer.toString(stream_id) + ": " + Integer.toString(
//                            frames.poll().first
//                        )
//                    )
//                }
//            }

            //update data
//            synchronized(drawingData) {
//                drawingData.frame = frame
//                drawingData.frame_id = data.frame_id
//                drawingData.updated = true
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

    fun updateCapture() {
        // force free resources otherwise licence error may occur when create sdk object in next time
//        capturer.dispose()
        videoWorker.dispose()


//        val captureConf = faceRecService.Config("fda_tracker_capturer.xml")
//        captureConf.overrideParameter("downscale_rawsamples_to_preferred_size", 0.0)


//        capturer = faceRecService.createCapturer(captureConf)
//        videoWorker = faceRecService.createVideoWorker(
//            captureConf,
//            "",
//            STREAMS_COUNT,
//            PROCESSING_THREADS_COUNT,
//            MATCHING_THREADS_COUNT
//        )
    }

    fun setTextView() {
        textView = activity.findViewById<View>(R.id.tvData) as TextView
    }


//    fun processingImage(canvas: Canvas, data: ByteArray?, width: Int, height: Int) {
//        val paint = Paint()
//        paint.color = -0x10000
//        paint.strokeWidth = 3f
//        paint.style = Paint.Style.STROKE
//        var text = ""
//        val rawImage = RawImage(width, height, RawImage.Format.FORMAT_YUV_NV21, data)
//        val samples = capturer.capture(rawImage)
//        if (samples.isEmpty()) return
//
//        // output info for one person
//        val sample = samples[0]
//
//        // face rectangle
//        if (flagRectangle) {
//            val rect = sample.rectangle
//            canvas.drawRect(
//                rect.x.toFloat(),
//                rect.y.toFloat(),
//                rect.x + rect.width.toFloat(),
//                rect.y + rect.height.toFloat(),
//                paint
//            )
//        }
//
//        // head angles
//        if (flagAngles) {
//            val angles = sample.angles
//            text += """Angles:
//	yaw:	${angles.yaw}
//	pitch:	${angles.pitch}
//	roll:	${angles.roll}
//"""
//        }
//
//        // quality
//        if (flagQuality) {
//            val quality = qualityEstimator!!.estimateQuality(sample)
//            text += """Quality:
//	lighting:	${quality.lighting}
//	noise:	${quality.noise}
//	sharpness:	${quality.sharpness}
//	flare:	${quality.flare}
//"""
//        }
//
//        // liveness
//        if (flagLiveness) {
//            // here we get/create the liveness estimator that work with this face
//            val id = sample.id
//            if (!id2le.containsKey(id)) {
//                id2le[id] = faceRecService!!.createLivenessEstimator()
//            }
//            val le = id2le[id]
//
//            // add information to the estimator
//            le!!.addSample(sample)
//
//            // get liveness
//            val liveness = le.estimateLiveness()
//            text += """
//                Liveness: ${liveness.name}
//
//                """.trimIndent()
//        }
//
//        // age and gender
//        if (flagAgeAndGender) {
//            val ageGender = ageGenderEstimator!!.estimateAgeGender(sample)
//            text += "Age: " + (ageGender.age_years + 0.5).toInt() + " years - "
//
//            Log.i(TAG, "Age: " + (ageGender.age_years + 0.5).toInt() + " years - ")
//
//            text += when (ageGender.age) {
//                Age.AGE_KID -> "kid\n"
//                Age.AGE_YOUNG -> "young\n"
//                Age.AGE_ADULT -> "adult\n"
//                Age.AGE_SENIOR -> "senior\n"
//            }
//            text += when (ageGender.gender) {
//                Gender.GENDER_FEMALE -> "Gender: female\n"
//                Gender.GENDER_MALE -> "Gender: male\n"
//            }
//        }
//
//        // crops
//        if (faceCutType != null) {
//            val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//            val cutBorderPaint = Paint()
//            cutBorderPaint.color = -0xaaab
//            cutBorderPaint.strokeWidth = 3f
//            cutBorderPaint.style = Paint.Style.STROKE
//            val os: OutputStream = ByteArrayOutputStream()
//            sample.cutFaceImage(os, RawSample.ImageFormat.IMAGE_FORMAT_JPG, faceCutType)
//            val byteCrop = (os as ByteArrayOutputStream).toByteArray()
//            val bitmapCrop = BitmapFactory.decodeByteArray(byteCrop, 0, byteCrop.size)
//            val srcRect = Rect(0, 0, bitmapCrop.width, bitmapCrop.height)
//            val divider = 4.0
//            val dstWidth = width / divider
//            val k = dstWidth / bitmapCrop.width
//            val dstHeight = bitmapCrop.height * k
//            val dstRect = Rect(0, 0, dstWidth.toInt(), dstHeight.toInt())
//            canvas.drawBitmap(bitmapCrop, srcRect, dstRect, bitmapPaint)
//            canvas.drawRect(dstRect, cutBorderPaint)
//        }
//
//        // points
//        // all points - red
//        // left eye - green
//        // right eye - yellow
//        if (flagPoints) {
//            val points = sample.landmarks
//            val leftEye = sample.leftEye
//            val rightEye = sample.rightEye
//            paint.strokeWidth = 3f
//            for (point in points) {
//                canvas.drawCircle(point.x, point.y, 1f, paint)
//            }
//            paint.strokeWidth = 2f
//            paint.color = -0xff0100
//            canvas.drawCircle(leftEye.x, leftEye.y, 3f, paint)
//            paint.color = -0x100
//            canvas.drawCircle(rightEye.x, rightEye.y, 3f, paint)
//        }
//
//        // face quality
//        if (flagFaceQuality) {
//            val faceQuality = faceQualityEstimator!!.estimateQuality(sample)
//            text += "Face quality: $faceQuality\n"
//        }
//
//        // angles vectors
//        if (flagAnglesVectors) {
//            val lEye = sample.leftEye
//            val rEye = sample.rightEye
//            val headAngles = sample.angles
//            val faceRect = sample.rectangle
//            val center = Point((lEye.x + rEye.x) / 2, (lEye.y + rEye.y) / 2)
//            val length = (faceRect.width + faceRect.height) * 0.3f
//            val cosYaw = cos(headAngles.yaw * Math.PI / 180).toFloat()
//            val sinYaw = sin(headAngles.yaw * Math.PI / 180).toFloat()
//            val cosPitch = cos(headAngles.pitch * Math.PI / 180).toFloat()
//            val sinPitch = sin(headAngles.pitch * Math.PI / 180).toFloat()
//            val cosRoll = cos(headAngles.roll * Math.PI / 180).toFloat()
//            val sinRoll = sin(headAngles.roll * Math.PI / 180).toFloat()
//            val xyz =
//                arrayOf(
//                    Point(
//                        cosYaw * cosRoll - sinYaw * sinPitch * sinRoll,
//                        sinYaw * sinPitch * cosRoll + cosYaw * sinRoll
//                    ),
//                    Point(
//                        cosPitch * sinRoll,
//                        -cosPitch * cosRoll
//                    ),
//                    Point(
//                        sinYaw * cosRoll + cosYaw * sinPitch * sinRoll,
//                        -cosYaw * sinPitch * cosRoll + sinYaw * sinRoll
//                    )
//                )
//            paint.strokeWidth = 3f
//            paint.color = -0x100
//            canvas.drawLine(
//                center.x,
//                center.y,
//                center.x + xyz[0].x * length,
//                center.y + xyz[0].y * length,
//                paint
//            )
//            paint.color = -0xff0100
//            canvas.drawLine(
//                center.x,
//                center.y,
//                center.x + xyz[1].x * length,
//                center.y + xyz[1].y * length,
//                paint
//            )
//            paint.color = -0x10000
//            canvas.drawLine(
//                center.x,
//                center.y,
//                center.x + xyz[2].x * length,
//                center.y + xyz[2].y * length,
//                paint
//            )
//        }
//
//        // emotions
//        if (flagEmotions) {
//            val emotionsConf = emotionsEstimator!!.estimateEmotions(sample)
//            activity.findViewById<View>(R.id.emotions).visibility = View.VISIBLE
//            for (ec in emotionsConf) {
//                when (ec.emotion) {
//                    Emotion.EMOTION_NEUTRAL -> {
//                        setWeight(R.id.neutral1, ec.confidence)
//                        setWeight(R.id.neutral2, 1 - ec.confidence)
//                    }
//                    Emotion.EMOTION_HAPPY -> {
//                        setWeight(R.id.happy1, ec.confidence)
//                        setWeight(R.id.happy2, 1 - ec.confidence)
//                    }
//                    Emotion.EMOTION_ANGRY -> {
//                        setWeight(R.id.angry1, ec.confidence)
//                        setWeight(R.id.angry2, 1 - ec.confidence)
//                    }
//                    Emotion.EMOTION_SURPRISE -> {
//                        setWeight(R.id.surprise1, ec.confidence)
//                        setWeight(R.id.surprise2, 1 - ec.confidence)
//                    }
//                }
//            }
//        } else {
//            activity.findViewById<View>(R.id.emotions).visibility = View.GONE
//        }
//        textView!!.text = text
//
//        Log.i(TAG, text)
//    }

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
//        capturer.dispose()
//        qualityEstimator?.dispose()
//        ageGenderEstimator?.dispose()
//        emotionsEstimator?.dispose()
//        faceQualityEstimator?.dispose()
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


        // get RawImage
        val frame = RawImage(
            width,
            height,
            RawImage.Format.FORMAT_YUV_NV21,
            data
        )

        val argb = Converter_YUV_NV_2_ARGB.convert_yuv_nv_2_argb(false, data, width, height)

        val immut_bitmap = Bitmap.createBitmap(
            argb,
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
//        val mut_bitmap = immut_bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val frame_id = videoWorker.addVideoFrame(frame, stream_id)
        Log.e(TAG, frame_id.toString())
//        frames.offer(Pair(frame_id, mut_bitmap))
    }


}