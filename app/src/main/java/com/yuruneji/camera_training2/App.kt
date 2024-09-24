package com.yuruneji.camera_training2

import android.app.Application
import android.content.Context
import com.yuruneji.camera_training2.domain.usecase.LogFile
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var logFile: LogFile

    override fun onCreate() {
        super.onCreate()

        // Timber.plant(Timber.DebugTree())
        Timber.plant(LogTree(context = this, logFile = logFile))
    }

    class LogTree(private val context: Context, private val logFile: LogFile) : Timber.DebugTree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            super.log(priority, tag, message, t)
            logFile.postLog(context, priority, tag, message, t)
        }
    }
}
