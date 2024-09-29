package com.yuruneji.camera_training2.common

/**
 * 通信レスポンス
 * @author toru
 * @version 1.0
 */
sealed class CardFaceAuthNetworkResponse<T>(
    val data: T? = null,
    val error: Throwable? = null,
) {
    class Success<T>(data: T) : CardFaceAuthNetworkResponse<T>(data = data)
    class Failure<T>(error: Throwable) : CardFaceAuthNetworkResponse<T>(error = error)
    class Loading<T> : CardFaceAuthNetworkResponse<T>()
}
