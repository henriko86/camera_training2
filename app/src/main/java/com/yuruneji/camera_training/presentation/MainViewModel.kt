package com.yuruneji.camera_training.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    //
) : ViewModel() {

    private val _mainState = MutableLiveData<LocalDateTime>()
    val mainState: LiveData<LocalDateTime> = _mainState

    private var mainJob: Job? = null

    fun startMainJob() {
        mainJob = viewModelScope.launch {
            while (isActive) {
                delay(10_000L)
            }
        }
    }

    fun cancelMainJob() {
        viewModelScope.launch {
            mainJob?.cancelAndJoin()
        }
    }

}
