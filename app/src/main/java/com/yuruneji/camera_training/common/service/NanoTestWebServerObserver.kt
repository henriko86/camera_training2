package com.yuruneji.camera_training.common.service

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author toru
 * @version 1.0
 */
class NanoTestWebServerObserver(
    private val port: Int = 8888,
    private val callback: (keyA: String, keyB: String) -> Unit
) : DefaultLifecycleObserver {

    /** TestWebServer */
    private var testWebServer: NanoTestWebServer? = null

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        testWebServer = NanoTestWebServer(port, callback)
        testWebServer?.start()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        testWebServer?.stop()
    }
}
