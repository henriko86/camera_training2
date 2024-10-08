package com.yuruneji.camera_training.domain.usecase

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author toru
 * @version 1.0
 */
class TestWebServerService(
    private val port: Int = 8888,
    private val callback: (keyA: String, keyB: String) -> Unit
) : DefaultLifecycleObserver {

    /** TestWebServer */
    private var testWebServer: TestWebServer? = null

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        testWebServer = TestWebServer(port, callback)
        testWebServer?.start()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        testWebServer?.stop()
    }
}
