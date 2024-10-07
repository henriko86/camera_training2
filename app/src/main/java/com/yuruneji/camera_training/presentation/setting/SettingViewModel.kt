package com.yuruneji.camera_training.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuruneji.camera_training.data.local.preference.CameraPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val cameraPref: CameraPreferences
) : ViewModel() {


    /** 使用カメラ */
    private val _lensFacing = MutableLiveData(cameraPref.lensFacing)

    /** 使用カメラ */
    val lensFacing: LiveData<Int> = _lensFacing

    /**
     * 使用カメラ
     */
    fun updateLensFacing(value: Int) {
        cameraPref.lensFacing = value
    }

    /** APIタイプ */
    private val _apiType = MutableLiveData(cameraPref.apiType)

    /** APIタイプ */
    val apiType: LiveData<Int> = _apiType

    /**
     * APIタイプ
     */
    fun updateApiType(value: Int) {
        cameraPref.apiType = value
    }

    /** 認証方法 単要素認証,多要素認証 */
    private val _authMethod = MutableLiveData(cameraPref.authMethod)

    /** 認証方法 単要素認証,多要素認証 */
    val authMethod: LiveData<Int> = _authMethod

    /**
     * 認証方法 単要素認証,多要素認証
     */
    fun updateAuthMethod(value: Int) {
        cameraPref.authMethod = value
    }

    /** 多要素認証 カード＆顔認証,QRコード＆顔認証 */
    private val _multiAuthType = MutableLiveData(cameraPref.multiAuthType)

    /** 多要素認証 カード＆顔認証,QRコード＆顔認証 */
    val multiAuthType: LiveData<Int> = _multiAuthType

    /**
     * 多要素認証 カード＆顔認証,QRコード＆顔認証
     */
    fun updateMultiAuthType(value: Int) {
        cameraPref.multiAuthType = value
    }

    /** 顔認証 */
    private val _faceAuth = MutableLiveData(cameraPref.faceAuth)

    /** 顔認証 */
    val faceAuth: LiveData<Boolean> = _faceAuth

    /**
     * 顔認証
     */
    fun updateFaceAuth(value: Boolean) {
        cameraPref.faceAuth = value
    }

    /** カード認証 */
    private val _cardAuth = MutableLiveData(cameraPref.cardAuth)

    /** カード認証 */
    val cardAuth: LiveData<Boolean> = _cardAuth

    /**
     * カード認証
     */
    fun updateCardAuth(value: Boolean) {
        cameraPref.cardAuth = value
    }

    /** QRコード認証 */
    private val _qrAuth = MutableLiveData(cameraPref.qrAuth)

    /** QRコード認証 */
    val qrAuth: LiveData<Boolean> = _qrAuth

    /**
     * QRコード認証
     */
    fun updateQrAuth(value: Boolean) {
        cameraPref.qrAuth = value
    }

    /** 最小顔サイズ */
    private val _minFaceSize = MutableLiveData(cameraPref.minFaceSize)

    /** 最小顔サイズ */
    val minFaceSize: LiveData<Float> = _minFaceSize

    /**
     * 最小顔サイズを更新
     */
    fun updateMinFaceSize(value: Float) {
        cameraPref.minFaceSize = value
    }


}
