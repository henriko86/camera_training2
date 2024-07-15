package com.yuruneji.cameratraining2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * @author toru
 * @version 1.0
 */
@HiltAndroidApp
class App:Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}
