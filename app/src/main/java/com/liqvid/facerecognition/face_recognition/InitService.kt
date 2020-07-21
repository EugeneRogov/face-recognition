package com.liqvid.facerecognition.face_recognition

import android.util.Log
import com.liqvid.facerecognition.ui.MainActivity
import com.vdt.face_recognition.sdk.FacerecService
import io.reactivex.rxjava3.core.Observable
import ru.liqvid.util.Constant

class InitService {
    companion object {
        val TAG: String = InitService::class.java.simpleName
    }

    /**
     * Check licence and init service
     *
     * @see FacerecService
     *
     * @param dllPath
     * @throws Exception
     */

    @Throws(Exception::class)
    fun initFaceRecService(dllPath: String): Observable<FacerecService> {
        return Observable.create { subscriber ->
            try {
                val service = FacerecService.createService(
                    dllPath,
                    Constant.FR_CONF_PATH,
                    ""
                )

                val licenseState = service.licenseState

                Log.i(TAG, "license_state.online            = " + licenseState.online)
                Log.i(TAG, "license_state.android_app_id    = " + licenseState.android_app_id)
                Log.i(TAG, "license_state.ios_app_id        = " + licenseState.ios_app_id)
                Log.i(TAG, "license_state.hardware_reg      = " + licenseState.hardware_reg)

                subscriber.onNext(service)
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }
    }

    @Throws(Exception::class)
    fun initFaceRecognition(mainActivity: MainActivity, service: FacerecService): Observable<FaceRecognition> {
        return Observable.create { subscriber ->
            try {
                val faceRecognition =
                    FaceRecognition(
                        mainActivity,
                        service
                    )
                subscriber.onNext(faceRecognition)
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }
    }

    @Throws(Exception::class)
    fun initCamera(mainActivity: MainActivity): Observable<TheCamera> {
        return Observable.create { subscriber ->
            try {
                val camera =
                    TheCamera(
                        mainActivity
                    )
                subscriber.onNext(camera)
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }
    }



}