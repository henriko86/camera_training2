package com.yuruneji.camera_training.common.response

/**
 * 通信レスポンス
 * @author toru
 * @version 1.0
 */
sealed class AuthResponse<T1, T2>(
    val req: T1? = null,
    val resp: T2? = null,
    val error: Throwable? = null,
) {
    class Success<T1, T2>(req: T1, resp: T2) : AuthResponse<T1, T2>(req = req, resp = resp)
    class Failure<T1, T2>(req: T1, error: Throwable) : AuthResponse<T1, T2>(req = req, error = error)
    class Loading<T1, T2> : AuthResponse<T1, T2>()
}
