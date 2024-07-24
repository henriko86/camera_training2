package com.yuruneji.cameratraining2.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuruneji.cameratraining2.common.DataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val dataProvider: DataProvider
) : ViewModel() {
    //


    private val _userName = MutableLiveData<String>().apply {
        value = dataProvider.getUserName()
    }
    val userName: LiveData<String> = _userName

    fun saveUserName(name: String) {
        dataProvider.setUserName(name)
    }

}
