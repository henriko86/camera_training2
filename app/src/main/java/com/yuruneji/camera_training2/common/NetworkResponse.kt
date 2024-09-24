package com.yuruneji.camera_training2.common

/**
 * 通信レスポンス
 * @author toru
 * @version 1.0
 */
sealed class NetworkResponse<T>(
    val data: T? = null,
    val error: Throwable? = null,
) {
    class Success<T>(data: T) : NetworkResponse<T>(data = data)
    class Failure<T>(error: Throwable) : NetworkResponse<T>(error = error)
    class Loading<T> : NetworkResponse<T>()
}
