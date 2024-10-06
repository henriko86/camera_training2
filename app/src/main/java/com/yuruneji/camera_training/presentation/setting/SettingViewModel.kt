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
    //

    private val _lensFacing = MutableLiveData(cameraPref.getLensFacing())
    val lensFacing: LiveData<Int> = _lensFacing

    fun updateLensFacing(value: Int) {
        cameraPref.setLensFacing(value)
    }


    private val _apiType = MutableLiveData(cameraPref.getApiType())
    val apiType: LiveData<Int> = _apiType

    fun updateApiType(value: Int) {
        cameraPref.setApiType(value)
    }


    private val _authMethod = MutableLiveData(cameraPref.getAuthMethod())
    val authMethod: LiveData<Int> = _authMethod

    fun updateAuthMethod(value: Int) {
        cameraPref.setAuthMethod(value)
    }


    private val _multiAuthType = MutableLiveData(cameraPref.getMultiAuthType())
    val multiAuthType: LiveData<Int> = _multiAuthType

    fun updateMultiAuthType(value: Int) {
        cameraPref.setMultiAuthType(value)
    }


    private val _faceAuth = MutableLiveData(cameraPref.isFaceAuth())
    val faceAuth: LiveData<Boolean> = _faceAuth

    fun updateFaceAuth(value: Boolean) {
        cameraPref.setFaceAuth(value)
    }


    private val _cardAuth = MutableLiveData(cameraPref.isCardAuth())
    val cardAuth: LiveData<Boolean> = _cardAuth

    fun updateCardAuth(value: Boolean) {
        cameraPref.setCardAuth(value)
    }


    private val _qrAuth = MutableLiveData(cameraPref.isQrAuth())
    val qrAuth: LiveData<Boolean> = _qrAuth

    fun updateQrAuth(value: Boolean) {
        cameraPref.setQrAuth(value)
    }

    private val _minFaceSize = MutableLiveData(cameraPref.getMinFaceSize())
    val minFaceSize: LiveData<Float> = _minFaceSize

    fun updateMinFaceSize(value: Float) {
        cameraPref.setMinFaceSize(value)
    }


}
