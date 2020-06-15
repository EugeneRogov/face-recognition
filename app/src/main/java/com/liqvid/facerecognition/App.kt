package com.liqvid.facerecognition

import android.content.Context
import androidx.multidex.MultiDexApplication
import ru.liqvid.data.di.NetworkComponent

class App : MultiDexApplication() {

    companion object {
        lateinit var instance: App

        fun get(context: Context): App? {
            return context.applicationContext as? App
        }
    }

    private var networkComponent: NetworkComponent? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
//        networkComponent = buildNetworkComponent()
    }

//    private fun buildNetworkComponent(): NetworkComponent? {
//        return DaggerNetworkComponent.builder()
//            .build()
//    }

}