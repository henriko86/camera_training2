package com.yuruneji.camera_training.data.local.preference

import android.content.Context
import com.yuruneji.camera_training.data.local.preference.CameraPreferences.Companion

/**
 * @author toru
 * @version 1.0
 */
class AppPreferences(context: Context) : BasePreferences(context, CameraPreferences.PREF_NAME) {
    companion object {
        const val PREF_NAME = "app_preferences"

        /** 使用カメラ */
        private const val LENS_FACING = "lens_facing"

        /** APIタイプ*/
        private const val API_TYPE = "api_type"

        /** 認証方法 [単要素認証,多要素認証] */
        private const val AUTH_METHOD = "auth_method"

        /** 多要素認証 [カード＆顔認証,QRコード＆顔認証] */
        private const val MULTI_AUTH_TYPE = "multi_auth_type"

        /** 顔認証 */
        private const val FACE_AUTH = "face_auth"

        /** カード認証 */
        private const val CARD_AUTH = "card_auth"

        /** QRコード認証 */
        private const val QR_AUTH = "qr_auth"

        /** 最小顔サイズ */
        private const val MIN_FACE_SIZE = "min_face_size"
    }

    /**
     * 使用カメラ
     */
    var lensFacing: Int
        get() = getInt(LENS_FACING, 0)
        set(value) = setInt(LENS_FACING, value)

    /**
     * APIタイプ
     */
    var apiType: Int
        get() = getInt(API_TYPE, 0)
        set(value) = setInt(API_TYPE, value)

    /**
     * 認証方法 [単要素認証,多要素認証]
     */
    var authMethod: Int
        get() = getInt(AUTH_METHOD, 0)
        set(value) = setInt(AUTH_METHOD, value)

    /**
     * 多要素認証 [カード＆顔認証,QRコード＆顔認証]
     */
    var multiAuthType: Int
        get() = getInt(MULTI_AUTH_TYPE, 0)
        set(value) = setInt(MULTI_AUTH_TYPE, value)

    /**
     * 顔認証
     */
    var faceAuth: Boolean
        get() = getBoolean(FACE_AUTH, false)
        set(value) = setBoolean(FACE_AUTH, value)

    /**
     * カード認証
     */
    var cardAuth: Boolean
        get() = getBoolean(CARD_AUTH, false)
        set(value) = setBoolean(CARD_AUTH, value)

    /**
     * QRコード認証
     */
    var qrAuth: Boolean
        get() = getBoolean(QR_AUTH, false)
        set(value) = setBoolean(QR_AUTH, value)

    /**
     * 最小顔サイズ
     */
    var minFaceSize: Float
        get() = getFloat(MIN_FACE_SIZE, 0.15f)
        set(value) = setFloat(MIN_FACE_SIZE, value)

}
