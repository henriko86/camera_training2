package com.yuruneji.camera_training2.presentation.setting.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingHomeViewModel @Inject constructor(
    //
) : ViewModel() {
    // TODO: Implement the ViewModel


    fun hoge() {
        // viewModelScope.launch {
        //     val handler = CoroutineExceptionHandler { _, exception ->
        //         println("CoroutineExceptionHandler got $exception with suppressed ${exception.suppressed.contentToString()}")
        //     }
        //
        //     val job = launch {
        //         launch {
        //             try {
        //                 delay(Long.MAX_VALUE) // これは他の兄弟がIOExceptionで失敗するとき、キャンセルされる
        //             } finally {
        //                 throw ArithmeticException() // 二番目の例外
        //             }
        //         }
        //         launch {
        //             delay(100)
        //             throw IOException() // 最初の例外
        //         }
        //         delay(Long.MAX_VALUE)
        //     }
        //     job.join()
        // }
    }


}
