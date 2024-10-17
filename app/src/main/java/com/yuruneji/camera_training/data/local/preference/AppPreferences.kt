package com.yuruneji.camera_training.data.local.preference

import android.content.Context

/**
 * @author toru
 * @version 1.0
 */
class AppPreferences(context: Context) : BasePreferences(context, PREF_NAME, PREF_ENCRYPT_NAME) {

    companion object {
        /** 設定ファイル名 */
        const val PREF_NAME = "app_preferences"

        /** 設定ファイル名 */
        const val PREF_ENCRYPT_NAME = "app_encrypt_preferences"


        /** 使用カメラ */
        private const val LENS_FACING = "lens_facing"

        /** 画像幅 */
        private const val IMAGE_WIDTH = "imageWidth"

        /** 画像高さ */
        private const val IMAGE_HEIGHT = "imageHeight"

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
     * 画像幅
     */
    var imageWidth: Int
        get() = getInt(IMAGE_WIDTH, 480)
        set(value) = setInt(IMAGE_WIDTH, value)

    /**
     * 画像高さ
     */
    var imageHeight: Int
        get() = getInt(IMAGE_HEIGHT, 640)
        set(value) = setInt(IMAGE_HEIGHT, value)

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

/**
 * AppPreferencesをAppSettingModelに変換
 * @return
 */
fun AppPreferences.convert(): AppSettingModel {
    return AppSettingModel(
        lensFacing = lensFacing,
        imageWidth = imageWidth,
        imageHeight = imageHeight,
        apiType = apiType,
        authMethod = authMethod,
        multiAuthType = multiAuthType,
        faceAuth = faceAuth,
        cardAuth = cardAuth,
        qrAuth = qrAuth,
        minFaceSize = minFaceSize
    )
}

/**
 * 設定を更新
 * @param model
 */
fun AppPreferences.import(model: AppSettingModel) {
    lensFacing = model.lensFacing
    imageWidth = model.imageWidth
    imageHeight = model.imageHeight
    apiType = model.apiType
    authMethod = model.authMethod
    multiAuthType = model.multiAuthType
    faceAuth = model.faceAuth
    cardAuth = model.cardAuth
    qrAuth = model.qrAuth
    minFaceSize = model.minFaceSize
}
