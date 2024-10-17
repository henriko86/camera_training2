package com.yuruneji.camera_training.common.response

/**
 * 通信レスポンス
 * @author toru
 * @version 1.0
 */
sealed class DeviceResponse<T>(
    val data: T? = null,
    val error: Throwable? = null,
) {
    class Success<T>(data: T) : DeviceResponse<T>(data = data)
    class Failure<T>(error: Throwable) : DeviceResponse<T>(error = error)
    class Loading<T> : DeviceResponse<T>()
}
