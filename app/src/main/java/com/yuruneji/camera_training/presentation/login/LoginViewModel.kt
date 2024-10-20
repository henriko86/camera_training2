package com.yuruneji.camera_training.presentation.login

import androidx.lifecycle.ViewModel
import com.yuruneji.camera_training.data.local.setting.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val pref: AppPreferences
) : ViewModel() {
    //
}
