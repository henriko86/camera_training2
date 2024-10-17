package com.yuruneji.camera_training.common.service

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author toru
 * @version 1.0
 */
class KtorWebServer(
    private val port: Int,
    private val callback: Callback,
) {

    interface Callback {
        fun onKtorReceive(ids: List<String>)
        fun onKtorFailure(t: Throwable)
    }

    private var server: ApplicationEngine? = null

    fun start() {
        server = embeddedServer(Netty, port = port) {

            install(CallLogging)
            install(ContentNegotiation) {
                json()
            }

            // Basic認証
            // install(Authentication) {
            //     basic {
            //         validate { credentials ->
            //             if (credentials.name == "" && credentials.password == "") {
            //                 UserIdPrincipal(credentials.name)
            //             } else {
            //                 null
            //             }
            //         }
            //     }
            // }

            install(Authentication)


            install(Routing) {
                post("/hoge") {
                    try {
                        val req = call.receive<RegisterRequest>()
                        callback.onKtorReceive(req.ids)

                        val resp = RegisterResponse(result = 0)
                        call.respond(resp)
                    } catch (e: Exception) {
                        callback.onKtorFailure(e)

                        val resp = RegisterResponse(result = 1)
                        call.respond(resp)
                    }
                }
            }
        }
        server?.start(wait = true)
    }

    fun stop() {
        server?.stop(1000, 1000)
    }
}

@Serializable
data class RegisterRequest(
    @SerialName("ids")
    val ids: List<String> = mutableListOf()
)

@Serializable
data class RegisterResponse(
    @SerialName("result")
    val result: Int
)
