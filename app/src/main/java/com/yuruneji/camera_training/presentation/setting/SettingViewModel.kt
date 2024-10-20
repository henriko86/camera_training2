package com.yuruneji.camera_training.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuruneji.camera_training.data.local.setting.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val pref: AppPreferences
) : ViewModel() {


    /** 使用カメラ */
    private val _lensFacing = MutableLiveData(pref.lensFacing)

    /** 使用カメラ */
    val lensFacing: LiveData<Int> = _lensFacing

    /**
     * 使用カメラ
     */
    fun updateLensFacing(value: Int) {
        pref.lensFacing = value
    }

    /** 画像幅 */
    private val _imageWidth = MutableLiveData(pref.imageWidth)

    /** 画像幅 */
    val imageWidth: LiveData<Int> = _imageWidth

    /**
     * 画像幅
     */
    fun updateImageWidth(value: Int) {
        pref.imageWidth = value
    }

    /** 画像高さ */
    private val _imageHeight = MutableLiveData(pref.imageHeight)

    /** 画像高さ */
    val imageHeight: LiveData<Int> = _imageHeight

    /**
     * 画像高さ
     */
    fun updateImageHeight(value: Int) {
        pref.imageHeight = value
    }

    /** APIタイプ */
    private val _apiType = MutableLiveData(pref.apiType)

    /** APIタイプ */
    val apiType: LiveData<Int> = _apiType

    /**
     * APIタイプ
     */
    fun updateApiType(value: Int) {
        pref.apiType = value
    }

    /** 認証方法 単要素認証,多要素認証 */
    private val _authMethod = MutableLiveData(pref.authMethod)

    /** 認証方法 単要素認証,多要素認証 */
    val authMethod: LiveData<Int> = _authMethod

    /**
     * 認証方法 単要素認証,多要素認証
     */
    fun updateAuthMethod(value: Int) {
        pref.authMethod = value
    }

    /** 多要素認証 カード＆顔認証,QRコード＆顔認証 */
    private val _multiAuthType = MutableLiveData(pref.multiAuthType)

    /** 多要素認証 カード＆顔認証,QRコード＆顔認証 */
    val multiAuthType: LiveData<Int> = _multiAuthType

    /**
     * 多要素認証 カード＆顔認証,QRコード＆顔認証
     */
    fun updateMultiAuthType(value: Int) {
        pref.multiAuthType = value
    }

    /** 顔認証 */
    private val _faceAuth = MutableLiveData(pref.faceAuth)

    /** 顔認証 */
    val faceAuth: LiveData<Boolean> = _faceAuth

    /**
     * 顔認証
     */
    fun updateFaceAuth(value: Boolean) {
        pref.faceAuth = value
    }

    /** カード認証 */
    private val _cardAuth = MutableLiveData(pref.cardAuth)

    /** カード認証 */
    val cardAuth: LiveData<Boolean> = _cardAuth

    /**
     * カード認証
     */
    fun updateCardAuth(value: Boolean) {
        pref.cardAuth = value
    }

    /** QRコード認証 */
    private val _qrAuth = MutableLiveData(pref.qrAuth)

    /** QRコード認証 */
    val qrAuth: LiveData<Boolean> = _qrAuth

    /**
     * QRコード認証
     */
    fun updateQrAuth(value: Boolean) {
        pref.qrAuth = value
    }

    /** 最小顔サイズ */
    private val _minFaceSize = MutableLiveData(pref.minFaceSize)

    /** 最小顔サイズ */
    val minFaceSize: LiveData<Float> = _minFaceSize

    /**
     * 最小顔サイズを更新
     */
    fun updateMinFaceSize(value: Float) {
        pref.minFaceSize = value
    }
}
