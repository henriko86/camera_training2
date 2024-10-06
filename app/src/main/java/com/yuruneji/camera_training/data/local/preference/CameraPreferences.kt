package com.yuruneji.camera_training.data.local.preference

import android.content.Context
import com.yuruneji.camera_training.presentation.camera.state.CameraSettingState

/**
 * @author toru
 * @version 1.0
 */
class CameraPreferences(context: Context) : BasePreferences(context, PREF_NAME) {

    companion object {
        const val PREF_NAME = "camera_preferences"

        private const val LENS_FACING = "lens_facing"
        private const val API_TYPE = "api_type"
        private const val AUTH_METHOD = "auth_method"
        private const val MULTI_AUTH_TYPE = "multi_auth_type"
        private const val FACE_AUTH = "face_auth"
        private const val CARD_AUTH = "card_auth"
        private const val QR_AUTH = "qr_auth"
        private const val MIN_FACE_SIZE = "min_face_size"
    }

    fun getLensFacing(): Int {
        return getInt(LENS_FACING, 0)
    }

    fun setLensFacing(value: Int) {
        setInt(LENS_FACING, value)
    }

    fun getApiType(): Int {
        return getInt(API_TYPE, 0)
    }

    fun setApiType(value: Int) {
        setInt(API_TYPE, value)
    }

    // AUTH_METHOD
    fun getAuthMethod(): Int {
        return getInt(AUTH_METHOD, 0)
    }

    fun setAuthMethod(value: Int) {
        setInt(AUTH_METHOD, value)
    }

    // MULTI_AUTH_TYPE
    fun getMultiAuthType(): Int {
        return getInt(MULTI_AUTH_TYPE, 0)
    }

    fun setMultiAuthType(value: Int) {
        setInt(MULTI_AUTH_TYPE, value)
    }

    // FACE_AUTH
    fun isFaceAuth(): Boolean {
        return getBoolean(FACE_AUTH, false)
    }

    fun setFaceAuth(value: Boolean) {
        setBoolean(FACE_AUTH, value)
    }

    // CARD_AUTH
    fun isCardAuth(): Boolean {
        return getBoolean(CARD_AUTH, false)
    }

    fun setCardAuth(value: Boolean) {
        setBoolean(CARD_AUTH, value)
    }


    // QR_AUTH
    fun isQrAuth(): Boolean {
        return getBoolean(QR_AUTH, false)
    }

    fun setQrAuth(value: Boolean) {
        setBoolean(QR_AUTH, value)
    }

    // MIN_FACE_SIZE
    fun getMinFaceSize(): Float {
        return getFloat(MIN_FACE_SIZE, 0.15f)
    }

    fun setMinFaceSize(value: Float) {
        setFloat(MIN_FACE_SIZE, value)
    }

}

fun CameraPreferences.convertCameraSettingState(): CameraSettingState {
    return CameraSettingState(
        lensFacing = getLensFacing(),
        authMethod = getAuthMethod(),
        multiAuthType = getMultiAuthType(),
        faceAuth = isFaceAuth(),
        cardAuth = isCardAuth(),
        qrAuth = isQrAuth()
    )
}
