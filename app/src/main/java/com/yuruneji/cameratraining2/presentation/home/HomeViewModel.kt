package com.yuruneji.cameratraining2.presentation.home

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuruneji.cameratraining2.common.DataProvider
import com.yuruneji.cameratraining2.common.DataStoreWrapper
import com.yuruneji.cameratraining2.common.NetworkResponse
import com.yuruneji.cameratraining2.data.remote.AppRequest
import com.yuruneji.cameratraining2.domain.model.FaceDetectDetail
import com.yuruneji.cameratraining2.domain.usecase.FaceAuthUseCase
import com.yuruneji.cameratraining2.presentation.home.state.AuthState
import com.yuruneji.cameratraining2.presentation.home.state.FaceAuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataProvider: DataProvider,
    private val dataStoreWrapper: DataStoreWrapper,
    private val faceAuthUseCase: FaceAuthUseCase
) : ViewModel() {

    private val _title = MutableLiveData<String>().apply {
        value = "ホーム画面"
    }
    val title: LiveData<String> = _title

    private val _cameraNone = MutableLiveData<Boolean>(true).apply {
        value = true
    }
    private val _cameraRgb = MutableLiveData<Boolean>(false)
    private val _cameraIr = MutableLiveData<Boolean>(false)
    val cameraNone: LiveData<Boolean> = _cameraNone
    val cameraRgb: LiveData<Boolean> = _cameraRgb
    val cameraIr: LiveData<Boolean> = _cameraIr


    private val _state = MutableStateFlow(FaceAuthState())
    val state: StateFlow<FaceAuthState> = _state

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private var faceAuthJob: Job? = null
    private var cardAuthJob: Job? = null
    private var postProcessJob: Job? = null

    // private val hogeController: HogeController

    private object PreferenceKeys {
        val KEY_NAME = stringPreferencesKey("KEY_NAME")
        val ID = intPreferencesKey("id")
        val NAME = stringPreferencesKey("name")
    }

    companion object {
        //
    }

    init {
        // hogeController = HogeController()

        viewModelScope.launch {
            Timber.i("HomeViewModel.init() ${getThreadName()}")

            delay(1000 * 15)
            // hogeController.stop()
        }

        dataProvider.setUserName("aaaaaa")
        val userName = dataProvider.getUserName()
        Timber.i("userName=${userName}")

        viewModelScope.launch(Dispatchers.Default) {
            dataStoreWrapper.writeValue(PreferenceKeys.NAME, "xxxxx")

            dataStoreWrapper.readValue(PreferenceKeys.NAME, "").collect {
                Timber.i("userName=${it}")
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            dataStoreWrapper.incrementCounter()
            dataStoreWrapper.exampleCounterFlow.collect {
                Timber.i("userName=${it}")
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            dataStoreWrapper.incrementCounter()
            //     dataStoreWrapper.exampleCounterFlow.collect {
            //         // Timber.i("userName=${it}")
            //     }
        }

    }

    suspend fun faceAuth(faceDetectDetail: FaceDetectDetail) {
        val img = "faceDetectDetail.faceBitmap"
        val rect = faceDetectDetail.faceRect
        // val center = faceDetectDetail.center

        if (faceAuthJob?.isActive != true && !_state.value.isLoading) {
            val request = AppRequest(img, rect.toString())

            Timber.d("顔認証開始... [" + getThreadName() + "]")
            Timber.d(request.toString() + " " + _state.value.isLoading)
            _authState.value = _authState.value.copy().apply {
                isFaceAuth = false
            }

            viewModelScope.launch {
                Timber.d("顔認証開始2... [" + getThreadName() + "]")

                withContext(Dispatchers.IO) {
                    Timber.d("顔認証開始5... Dispatchers.IO[" + getThreadName() + "]")

                    faceAuthJob = faceAuthUseCase(request).onEach { result ->
                        when (result) {
                            is NetworkResponse.Success -> {
                                // delay(2000)
                                _state.value = FaceAuthState(
                                    isLoading = false,
                                    faceAuth = result.data,
                                )
                                _authState.value = _authState.value.copy().apply {
                                    isFaceAuth = true
                                }
                                cardAuthJob?.cancel()
                                Timber.d("顔認証成功!!!! [" + getThreadName() + "]")
                            }

                            is NetworkResponse.Failure -> {
                                _state.value = FaceAuthState(error = result.error)
                                _authState.value = _authState.value.copy().apply {
                                    isFaceAuth = false
                                    isCardAuth = false
                                }
                                cardAuthJob?.cancel()
                                Timber.d("顔認証エラー [" + getThreadName() + "]")
                            }

                            is NetworkResponse.Loading -> {
                                _state.value = FaceAuthState(isLoading = true)
                                Timber.d("読み込み中..... [" + getThreadName() + "]")
                            }
                        }
                    }.launchIn(this)
                    faceAuthJob?.join()
                    Timber.d("終了..... [" + getThreadName() + "]")
                }
            }
        }
    }

    fun postProcess() {
        postProcessJob = viewModelScope.launch {
            Timber.d("認証後処理 start [" + getThreadName() + "]")

            withContext(Dispatchers.IO) {
                Timber.d("認証後処理1 [" + getThreadName() + "]")

                delay(3000)

                _authState.value = _authState.value.copy().apply {
                    isPostProcessAuth = true
                }

                Timber.d("認証後処理2 [" + getThreadName() + "]")
            }

            Timber.d("認証後処理 end [" + getThreadName() + "]")
        }
    }

    fun authStatusReset() {
        viewModelScope.launch {
            Timber.d("リセット処理 start [" + getThreadName() + "]")

            delay(5000)

            _authState.value = _authState.value.copy().apply {
                isFaceAuth = false
                isCardAuth = false
                isPostProcessAuth = false
            }

            Timber.d("リセット処理 end [" + getThreadName() + "]")
        }
    }

    private fun getThreadName(): String = Thread.currentThread().name

}
