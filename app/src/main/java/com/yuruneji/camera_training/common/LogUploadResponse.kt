package com.yuruneji.camera_training.common

/**
 * 通信レスポンス
 * @author toru
 * @version 1.0
 */
sealed class LogUploadResponse<T>(
    val data: T? = null,
    val error: Throwable? = null,
) {
    class Success<T>(data: T) : LogUploadResponse<T>(data = data)
    class Failure<T>(error: Throwable) : LogUploadResponse<T>(error = error)
    class Loading<T> : LogUploadResponse<T>()
}
