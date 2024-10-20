package com.yuruneji.camera_training.common.response

/**
 * 通信レスポンス
 * @author toru
 * @version 1.0
 */
sealed class DeviceResponse<T>(
    val response: T? = null,
    val error: Throwable? = null,
) {
    class Success<T>(response: T) : DeviceResponse<T>(response = response)
    class Failure<T>(error: Throwable) : DeviceResponse<T>(error = error)
    class Loading<T> : DeviceResponse<T>()
}
