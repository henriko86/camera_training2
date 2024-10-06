package com.yuruneji.camera_training.presentation.camera.state

/**
 * @author toru
 * @version 1.0
 */
data class CameraSettingState(
    /** 使用カメラ */
    val lensFacing: Int,
    /** 認証方法 [単要素認証,多要素認証] */
    val authMethod: Int,
    /** 多要素認証 [カード＆顔認証,QRコード＆顔認証] */
    val multiAuthType: Int,
    /** 顔認証 */
    val faceAuth: Boolean,
    /** カード認証 */
    val cardAuth: Boolean,
    /** QRコード認証 */
    val qrAuth: Boolean
)
