package com.yuruneji.camera_training.common.response

/**
 * 通信レスポンス
 * @author toru
 * @version 1.0
 */
sealed class CardFaceAuthResponse<T>(
    val data: T? = null,
    val error: Throwable? = null,
) {
    class Success<T>(data: T) : CardFaceAuthResponse<T>(data = data)
    class Failure<T>(error: Throwable) : CardFaceAuthResponse<T>(error = error)
    class Loading<T> : CardFaceAuthResponse<T>()
}
