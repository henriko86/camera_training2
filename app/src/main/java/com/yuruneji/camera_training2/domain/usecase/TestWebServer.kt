package com.yuruneji.camera_training2.domain.usecase

import fi.iki.elonen.NanoHTTPD
import timber.log.Timber

/**
 * @author toru
 * @version 1.0
 */
class TestWebServer(port: Int, private val callback: Callback) : NanoHTTPD(port) {

    interface Callback {
        fun onConnect(keyA: String, keyB: String)
    }

    override fun serve(session: IHTTPSession?): Response {
        session?.let {
            val method = session.method

            val uri = session.uri
            Timber.d("uri: $uri")

            for ((k, v) in session.headers) {
                Timber.d("ヘッダー key: $k, value: $v")
            }

            Timber.d(session.remoteHostName)
            Timber.d(session.remoteIpAddress)

            when (method) {
                Method.GET -> {
                    Timber.d("GET")
                }

                Method.POST -> {
                    Timber.d("POST")

                    for ((k, v) in session.parameters) {
                        Timber.d("key: $k, value: $v")
                    }
                    callback.onConnect(
                        session.parameters["keyA"]?.first() ?: "",
                        session.parameters["keyB"]?.first() ?: ""
                    )
                }

                else -> {}
            }
        }

        val msg = "<html><body><h1>Hello Web Server</h1></body></html>\n"
        return newFixedLengthResponse(msg)
    }

}
