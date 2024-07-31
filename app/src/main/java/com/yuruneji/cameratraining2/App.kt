package com.yuruneji.cameratraining2

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * @author toru
 * @version 1.0
 */
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        App.appContext=applicationContext
    }

    init {
        // instance = this
    }

    companion object {
        // var instance: App? = null

        // fun appContext(): Context {
        //     return instance!!.applicationContext
        // }

        lateinit var appContext: Context

    }
}
