package com.yuruneji.camera_training.presentation.setting_base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuruneji.camera_training.data.local.preference.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingBaseViewModel  @Inject constructor(
    private val pref: AppPreferences
): ViewModel() {

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

}
