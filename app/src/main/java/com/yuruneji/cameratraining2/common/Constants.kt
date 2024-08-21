package com.yuruneji.cameratraining2.common

import androidx.camera.core.CameraSelector

/**
 * 定数
 * @author toru
 * @version 1.0
 */
object Constants {

    /** URL */
    const val BASE_URL = "http://192.168.11.2:8080"

    /** カード認証　OR　顔認証 */
    const val AUTH_TYPE_CARD_OR_FACE = 0

    /** 顔認証 */
    const val AUTH_TYPE_FACE = 1

    /** カード認証 */
    const val AUTH_TYPE_CARD = 2

    /** カード認証　AND　顔認証 */
    const val AUTH_TYPE_CARD_AND_FACE = 3

    /** 前カメラ */
    const val LENS_FACING_FRONT = CameraSelector.LENS_FACING_FRONT

    /** 後ろカメラ */
    const val LENS_FACING_BACK = CameraSelector.LENS_FACING_BACK

}
