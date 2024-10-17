package com.yuruneji.camera_training.data.local.preference

/**
 * @author toru
 * @version 1.0
 */
data class AppSettingModel(
    /** 使用カメラ */
    val lensFacing: Int = 0,
    /** 画像幅 */
    val imageWidth: Int = 0,
    /** 画像高さ */
    val imageHeight: Int = 0,
    /** APIタイプ*/
    val apiType: Int = 0,
    /** 認証方法 [単要素認証,多要素認証] */
    val authMethod: Int = 0,
    /** 多要素認証 [カード＆顔認証,QRコード＆顔認証] */
    val multiAuthType: Int = 0,
    /** 顔認証 */
    val faceAuth: Boolean = false,
    /** カード認証 */
    val cardAuth: Boolean = false,
    /** QRコード認証 */
    val qrAuth: Boolean = false,
    /** 最小顔サイズ */
    val minFaceSize: Float = 0f,
)
