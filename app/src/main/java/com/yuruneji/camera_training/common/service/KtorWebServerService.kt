package com.yuruneji.camera_training.common.service

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber
import java.util.concurrent.Executors

/**
 * @author toru
 * @version 1.0
 */
class KtorWebServerService(
    private val port: Int,
    private val callback: KtorWebServer.Callback,
) : DefaultLifecycleObserver {

    private val executor = Executors.newSingleThreadExecutor()

    private var server: KtorWebServer? = null

    override fun onResume(owner: LifecycleOwner) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume(owner)

        executor.submit {
            server = KtorWebServer(port, callback)
            server?.start()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause(owner)

        executor.submit {
            server?.stop()
        }
    }
}
