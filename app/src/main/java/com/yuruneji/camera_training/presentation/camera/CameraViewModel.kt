package com.yuruneji.camera_training.presentation.camera

import android.content.Context
import android.graphics.Rect
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuruneji.camera_training.common.AuthMethod
import com.yuruneji.camera_training.common.MultiAuthType
import com.yuruneji.camera_training.common.response.FaceAuthResponse
import com.yuruneji.camera_training.common.response.DeviceResponse
import com.yuruneji.camera_training.common.service.NetworkService
import com.yuruneji.camera_training.common.service.SoundService
import com.yuruneji.camera_training.common.service.TimeService
import com.yuruneji.camera_training.common.toByteArray
import com.yuruneji.camera_training.data.local.preference.AppPreferences
import com.yuruneji.camera_training.data.local.preference.convert
import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.FaceItemModel
import com.yuruneji.camera_training.domain.model.QrItemModel
import com.yuruneji.camera_training.domain.usecase.CardAuthUseCase
import com.yuruneji.camera_training.domain.usecase.FaceAnalyzer
import com.yuruneji.camera_training.domain.usecase.FaceAuthUseCase
import com.yuruneji.camera_training.domain.usecase.LogUploadUseCase
import com.yuruneji.camera_training.domain.usecase.QrCodeAnalyzer
import com.yuruneji.camera_training.presentation.camera.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    // private val cameraPref: CameraPreferences,
    private val networkSensor: NetworkService,
    private val faceAuthUseCase: FaceAuthUseCase,
    private val cardAuthUseCase: CardAuthUseCase,
    private val logUploadUseCase: LogUploadUseCase,
) : ViewModel() {


    private val _drawFaceView = MutableStateFlow<List<Rect>>(mutableListOf())
    val drawFaceView: StateFlow<List<Rect>> = _drawFaceView.asStateFlow()

    private val _drawQrView = MutableStateFlow<List<Rect>>(mutableListOf())
    val drawQrView: StateFlow<List<Rect>> = _drawQrView.asStateFlow()


    /** 顔解析 */
    private var faceAnalyzer: FaceAnalyzer? = null

    /** QRコード解析 */
    private var qrCodeAnalyzer: QrCodeAnalyzer? = null


    /** 顔認証実行有無 */
    private val isFaceAuthState = AtomicBoolean(false)

    /** カード認証実行有無 */
    private val isCardAuthState = AtomicBoolean(false)

    /** QRコード認証実行有無 */
    private val isQrAuthState = AtomicBoolean(false)

    /**
     * カメラを開始
     * @param context     Context
     * @param owner       LifecycleOwner
     * @param previewView PreviewView
     */
    fun startCamera(
        context: Context,
        owner: LifecycleOwner,
        previewView: PreviewView
    ) {
        Timber.i(Throwable().stackTrace[0].methodName)


        val setting = AppPreferences(context).convert()

        val singleAuthFlag = setting.authMethod == AuthMethod.SINGLE.no
        val isMultiCardFaceAuth = setting.multiAuthType == MultiAuthType.CARD_FACE.no
        val isMultiQrFaceAuth = setting.multiAuthType == MultiAuthType.QR_FACE.no

        var isFaceAuth = false
        var isCardAuth = false
        var isQrAuth = false

        if (singleAuthFlag) { // 単要素認証
            if (setting.faceAuth) { // 顔認証
                isFaceAuth = true
            }
            if (setting.cardAuth) { // カード認証
                isCardAuth = true
            }
            if (setting.qrAuth) { // QRコード認証
                isQrAuth = true
            }
        } else { // 多要素認証
            if (isMultiCardFaceAuth) {
                isCardAuth = true
                isFaceAuth = true
                isCardAuthState.set(true)
            }
            if (isMultiQrFaceAuth) {
                isQrAuth = true
                isFaceAuth = true
                isQrAuthState.set(true)
            }
        }


        // 顔認証
        val faceAnalyzer = FaceAnalyzer(){
            faceAnalyze(it)
        }
        val faceImageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageRotationEnabled(true)
            .build()
        faceImageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), faceAnalyzer)

        // QRコード認証
        val barCodeScanner = QrCodeAnalyzer(){
            qrAnalyze(it)
        }
        val qrImageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageRotationEnabled(true)
            .build()
        qrImageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), barCodeScanner)


        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            // ライフサイクルにバインドするために利用する
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val useCaseGroupBuilder = UseCaseGroup.Builder().addUseCase(preview)
            if (isFaceAuth) {
                useCaseGroupBuilder.addUseCase(faceImageAnalysis)
            }
            if (isQrAuth) {
                useCaseGroupBuilder.addUseCase(qrImageAnalysis)
            }
            val useCaseGroup = useCaseGroupBuilder.build()

            // カメラを設定
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {
                // バインドされているカメラを解除
                cameraProvider.unbindAll()
                // カメラをライフサイクルにバインド
                cameraProvider.bindToLifecycle(
                    owner,
                    cameraSelector,
                    // preview,
                    // faceImageAnalysis,
                    // qrImageAnalysis,
                    useCaseGroup
                )
            } catch (exc: Exception) {
                Timber.e(exc, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * カメラを停止
     */
    fun stopCamera() {
        Timber.i(Throwable().stackTrace[0].methodName)
        faceAnalyzer?.close()
        qrCodeAnalyzer?.close()
    }

    // override fun onFaceDetect(list: List<FaceItemModel>) {
    //     faceAnalyze(list)
    // }
    //
    // override fun onQrCodeDetect(list: List<QrItemModel>) {
    //     qrAnalyze(list)
    // }

    /** 認証Job */
    private var authJob: Job? = null

    /** 認証中フラグ */
    private val authFlag = AtomicBoolean(false)

    /** 認証待ちプログレスバー */
    private val _authWaitProgressbar = MutableLiveData<Boolean>()

    /** 認証待ちプログレスバー */
    val authWaitProgressbar: LiveData<Boolean> = _authWaitProgressbar

    /**
     * 顔認証
     * @param list
     */
    private fun faceAnalyze(list: List<FaceItemModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            _drawFaceView.value = list.map { it.faceRect }

            if (isFaceAuthState.get()) {
                if (list.isNotEmpty()) {
                    faceAuth(list.first())
                }
            }
        }
    }

    /**
     * QRコード認証
     * @param itemList
     */
    private fun qrAnalyze(itemList: List<QrItemModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            _drawQrView.value = itemList.map { it.rect }

            if (isQrAuthState.get()) {
                if (itemList.isNotEmpty()) {
                    itemList.first().barcode.rawValue?.let {
                        cardAuth(it)
                    }
                }
            }
        }
    }

    /** 顔認証状態 */
    private val _faceAuthState = MutableLiveData<AuthState>()

    /** 顔認証状態 */
    val faceAuthState: LiveData<AuthState> = _faceAuthState

    /**
     * 顔認証を開始
     * @param faceItem 顔情報
     */
    private fun faceAuth(faceItem: FaceItemModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (authFlag.getAndSet(true)) {
                return@launch
            }

            Timber.d("顔認証 start")

            // faceItem.faceBitmap?.toByteArray()
            // val bmp: Bitmap? = faceItem.faceBitmap
            // val bmpWidth: Int = bmp?.width ?: 0
            // val bmpHeight: Int = bmp?.height ?: 0
            // val bmpByteArray: ByteArray? = faceItem.faceBitmap?.toByteArray()
            // val base64str = Base64.getEncoder().encodeToString(bmp);
            // val encodedStr: String = Base64.getEncoder().encodeToString(bmpByteArray)
            // val decodedByteArray: ByteArray? = Base64.getDecoder().decode(encodedStr)

            // var decodedBmp: Bitmap?
            // decodedByteArray?.let {
            //     try {
            //         // decodedBmp = BitmapFactory.decodeByteArray(decodedStr, 0, decodedStr.size)
            //         decodedBmp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
            //         decodedBmp?.copyPixelsFromBuffer(ByteBuffer.wrap(decodedByteArray))
            //     } catch (e: Exception) {
            //         Timber.e(e)
            //     }
            // }
            // Timber.d("")


            val model = AppRequestModel(
                img = "data:image/jpeg;base64,${Base64.getEncoder().encodeToString(faceItem.faceBitmap?.toByteArray())}"
            )

            soundService?.playAuthStart()

            authJob = faceAuthUseCase(model).onEach { result ->
                when (result) {
                    is FaceAuthResponse.Success -> {
                        Timber.d("顔認証 ${result.resp}")

                        soundService?.playAuthSuccess()
                        _faceAuthState.postValue(
                            AuthState(
                                req = result.req,
                                resp = result.resp
                            )
                        )
                    }

                    is FaceAuthResponse.Failure -> {
                        Timber.w("顔認証 ${result.error}")

                        soundService?.playAuthError()
                        _faceAuthState.postValue(
                            AuthState(
                                req = result.req,
                                error = result.error
                            )
                        )
                    }

                    is FaceAuthResponse.Loading -> {
                        Timber.d("顔認証 読み込み中.....")
                        _faceAuthState.postValue(
                            AuthState(
                                isLoading = true
                            )
                        )
                    }
                }
            }.launchIn(this)
            authJob?.join()

            _authWaitProgressbar.postValue(true)
            delay(10_000L)
            _authWaitProgressbar.postValue(false)

            authFlag.set(false)

            Timber.d("  顔認証 end")
        }
    }

    /** カード認証状態 */
    private val _cardAuthState = MutableLiveData<AuthState>()

    /** カード認証状態 */
    val cardAuthState: LiveData<AuthState> = _cardAuthState

    /**
     * カード認証を開始
     * @param authCode 認証コード
     */
    private fun cardAuth(authCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (authFlag.getAndSet(true)) {
                // return@launch
            }

            Timber.d("  カード認証 start isActive=${authJob?.isActive} isCompleted=${authJob?.isCompleted}")

            val model = AppRequestModel(
                card = authCode
            )


            if (authJob?.isActive == true) {
                return@launch
            }

            soundService?.playAuthStart()


            authJob = cardAuthUseCase(model).onEach { result ->
                when (result) {
                    is FaceAuthResponse.Success -> {
                        Timber.d("  カード認証 ${result.resp}")

                        soundService?.playAuthSuccess()
                        _cardAuthState.postValue(
                            AuthState(
                                req = result.req,
                                resp = result.resp
                            )
                        )
                    }

                    is FaceAuthResponse.Failure -> {
                        Timber.w("  カード認証 ${result.error}")

                        soundService?.playAuthError()
                        _cardAuthState.postValue(
                            AuthState(
                                req = result.req,
                                error = result.error
                            )
                        )
                    }

                    is FaceAuthResponse.Loading -> {
                        Timber.d("  カード認証 読み込み中.....")
                        _cardAuthState.postValue(
                            AuthState(
                                isLoading = true
                            )
                        )
                    }
                }
            }.launchIn(this)
            authJob?.join()

            _authWaitProgressbar.postValue(true)
            delay(10_000L)
            _authWaitProgressbar.postValue(false)

            authFlag.set(false)

            Timber.d("  カード認証 end")
        }
    }

    /**
     * 認証処理をキャンセル
     */
    fun cancelAuth() {
        viewModelScope.launch(Dispatchers.Default) {
            authJob?.cancelAndJoin()
        }
    }


    private val _authResultView = MutableLiveData<Boolean>()
    val authResultView: LiveData<Boolean> = _authResultView
    private var authResultViewJob: Job? = null

    fun setAuthResultView() {
        viewModelScope.launch(Dispatchers.Default) {
            Timber.d("${authResultViewJob?.isActive}")
            if (authResultViewJob?.isActive == true) {
                authResultViewJob?.cancelAndJoin()
            }

            authResultViewJob = launch {
                _authResultView.postValue(true)
                delay(5_000L)
                _authResultView.postValue(false)
            }
        }
    }


    /**
     * ログアップロードを開始
     * @param context Context
     */
    fun startLogUpload(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val today = LocalDateTime.now()
                val fileName = "${today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.log"
                val logFile = File(context.filesDir, fileName)

                logUpload(fileName, logFile)

                delay(60_000)
            }
        }
    }

    /**
     * ログアップロード
     * @param fileName ログファイル名
     * @param file     ログファイル
     */
    private suspend fun logUpload(fileName: String, file: File) = withContext(Dispatchers.IO) {
        val log = MultipartBody.Part.createFormData(
            "LogFile", fileName, file.asRequestBody("text/plain".toMediaType())
        )

        logUploadUseCase(log).onEach { result ->
            when (result) {
                is DeviceResponse.Success -> {
                    Timber.d("${result.data}")
                    Timber.d("ログアップロード!!!! [" + getThreadName() + "]")
                }

                is DeviceResponse.Failure -> {
                    Timber.w("${result.error}")
                    Timber.d("ログアップロードエラー [" + getThreadName() + "]")
                }

                is DeviceResponse.Loading -> {
                    Timber.d("ログアップロード 読み込み中..... [" + getThreadName() + "]")
                }
            }
        }.launchIn(this)
    }


    /** ネットワーク状態 */
    private val _networkState = MutableLiveData(false)

    /** ネットワーク状態 */
    val networkState: LiveData<Boolean> = _networkState

    /**
     * ネットワーク状態監視を開始
     * @param delayTime 監視間隔
     */
    fun startNetworkSensor(delayTime: Long = 10_000L) {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val result = networkSensor.checkNetworkAvailable()
                if (result != _networkState.value) {
                    _networkState.postValue(result)
                }

                delay(delayTime)
            }
        }
    }


    /** IPアドレス */
    private val _ipAddress = MutableLiveData<String>()

    /** IPアドレス */
    val ipAddress: LiveData<String> = _ipAddress

    /**
     * IPアドレスを取得
     */
    fun getIpAddress() {
        networkSensor.getIpAddress { ipAddress ->
            _ipAddress.postValue(ipAddress)
        }
    }


    /** 時刻チェック */
    private val _timeCheck = MutableLiveData<Boolean>()

    /** 時刻チェック */
    val timeCheck: LiveData<Boolean> = _timeCheck

    /**
     * 時刻チェックを開始
     * @param delayTime 監視間隔
     */
    fun startTimeCheck(delayTime: Long = 300_000L) {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                Timber.i("時刻チェック start (${getThreadName()})")
                _timeCheck.postValue(TimeService.isTimeSync())
                Timber.i("時刻チェック end (${getThreadName()})")

                delay(delayTime)
            }
        }
    }


    /** 効果音サービス */
    private var soundService: SoundService? = null

    /**
     * 効果音サービスを設定
     */
    fun setSoundService(soundService: SoundService) {
        this.soundService = soundService
    }

    /**
     * スレッド名を取得
     */
    private fun getThreadName(): String = Thread.currentThread().name
}
