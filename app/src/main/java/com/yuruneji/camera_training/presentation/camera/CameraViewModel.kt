package com.yuruneji.camera_training.presentation.camera

import android.content.Context
import android.graphics.Rect
import android.location.Location
import android.os.Handler
import android.os.Looper
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
import com.yuruneji.camera_training.common.BitmapUtils
import com.yuruneji.camera_training.common.MultiAuthState
import com.yuruneji.camera_training.common.MultiAuthType
import com.yuruneji.camera_training.common.response.DeviceResponse
import com.yuruneji.camera_training.common.response.FaceAuthResponse
import com.yuruneji.camera_training.common.service.NetworkService
import com.yuruneji.camera_training.common.service.NtpService
import com.yuruneji.camera_training.common.service.SoundId
import com.yuruneji.camera_training.data.local.setting.AppPreferences
import com.yuruneji.camera_training.data.local.setting.AppSettingModel
import com.yuruneji.camera_training.data.local.setting.convert
import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.FaceItemModel
import com.yuruneji.camera_training.domain.model.QrItemModel
import com.yuruneji.camera_training.domain.usecase.CardAuthUseCase
import com.yuruneji.camera_training.domain.usecase.FaceAnalyzer
import com.yuruneji.camera_training.domain.usecase.FaceAuthUseCase
import com.yuruneji.camera_training.domain.usecase.LogUploadUseCase
import com.yuruneji.camera_training.domain.usecase.QrCodeAnalyzer
import com.yuruneji.camera_training.presentation.camera.state.CameraScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class CameraViewModel @Inject constructor(
    pref: AppPreferences,
    private val networkService: NetworkService,
    private val faceAuthUseCase: FaceAuthUseCase,
    private val cardAuthUseCase: CardAuthUseCase,
    private val logUploadUseCase: LogUploadUseCase,
) : ViewModel() {


    /** 設定 */
    private var setting: AppSettingModel = pref.convert()


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

    // /** 多要素認証状態 */
    // private var multiAuthState: MultiAuthState = MultiAuthState.Before


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

        setting = AppPreferences(context).convert()

        val singleAuthFlag = setting.authMethod == AuthMethod.SINGLE
        val isMultiCardFaceAuth = setting.multiAuthType == MultiAuthType.CARD_FACE
        val isMultiQrFaceAuth = setting.multiAuthType == MultiAuthType.QR_FACE

        var isFaceAuth = false
        var isCardAuth = false
        var isQrAuth = false

        if (singleAuthFlag) { // 単要素認証
            if (setting.faceAuth) { // 顔認証
                isFaceAuth = true
                isFaceAuthState.set(true)
            }
            if (setting.cardAuth) { // カード認証
                isCardAuth = true
                isCardAuthState.set(true)
            }
            if (setting.qrAuth) { // QRコード認証
                isQrAuth = true
                isQrAuthState.set(true)
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
        faceAnalyzer = FaceAnalyzer {
            faceAnalyze(it)
        }
        val faceImageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageRotationEnabled(true)
            .build()
        faceAnalyzer?.let { faceAnalyzer ->
            faceImageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), faceAnalyzer)
        }

        // QRコード認証
        qrCodeAnalyzer = QrCodeAnalyzer {
            qrAnalyze(it)
        }
        val qrImageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageRotationEnabled(true)
            .build()
        qrCodeAnalyzer?.let { qrCodeAnalyzer ->
            qrImageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), qrCodeAnalyzer)
        }


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

            if (setting.authMethod == AuthMethod.SINGLE) {
                if (isFaceAuthState.get()) {
                    if (list.isNotEmpty()) {
                        faceAuth(list.first())
                    }
                }
            } else {
                if (isFaceAuthState.get()) {
                    if (list.isNotEmpty()) {
                        faceMultiAuth(list.first())
                    }
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

            if (setting.authMethod == AuthMethod.SINGLE) {
                if (isQrAuthState.get()) {
                    if (itemList.isNotEmpty()) {
                        itemList.first().barcode.rawValue?.let {
                            cardAuth(it)
                        }
                    }
                }
            } else {
                if (isQrAuthState.get()) {
                    if (itemList.isNotEmpty()) {
                        itemList.first().barcode.rawValue?.let {
                            cardMultiAuth(it)
                        }
                    }
                }
            }
        }
    }

    /** 顔認証状態 */
    private val _faceAuthState = MutableLiveData<CameraScreenState>()

    /** 顔認証状態 */
    val faceAuthState: LiveData<CameraScreenState> = _faceAuthState

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
                img = BitmapUtils.toBase64Jpeg(faceItem.faceBitmap) ?: ""
            )

            // soundService?.playAuthStart()
            _sound.postValue(SoundId.AUTH_START)

            authJob = faceAuthUseCase(model).onEach { result ->
                when (result) {
                    is FaceAuthResponse.Success -> {
                        Timber.d("顔認証 ${result.response}")

                        // soundService?.playAuthSuccess()
                        _sound.postValue(SoundId.AUTH_SUCCESS)
                        _faceAuthState.postValue(
                            CameraScreenState(
                                request = result.request,
                                response = result.response
                            )
                        )
                    }

                    is FaceAuthResponse.Failure -> {
                        Timber.w("顔認証 ${result.error}")

                        // soundService?.playAuthError()
                        _sound.postValue(SoundId.AUTH_ERROR)
                        _faceAuthState.postValue(
                            CameraScreenState(
                                request = result.request,
                                error = result.error
                            )
                        )
                    }

                    is FaceAuthResponse.Loading -> {
                        Timber.d("顔認証 読み込み中.....")
                        _faceAuthState.postValue(
                            CameraScreenState(
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

    // suspend fun faceAuth2(): String = suspendCancellableCoroutine { continuation ->
    //     val callback = Runnable {
    //         // 結果を c に渡す。キャンセルされている場合は何もしない
    //         if (continuation.isActive) {
    //             continuation.resume("晴れ")
    //         }
    //     }
    //
    //     val handler = Handler(Looper.getMainLooper())
    //     // coroutine のキャンセルが起きた時に Handler 側もキャンセルする
    //     continuation.invokeOnCancellation {
    //         handler.removeCallbacks(callback)
    //     }
    //
    //     // 2秒後に処理を呼び出すようタイマーを設定する。
    //     // callback 処理はメインスレッドで行われるが、それまでの間スレッドは解放される
    //     handler.postDelayed(callback, 5000)
    // }

    /**
     * 多要素顔認証を開始
     * @param faceItem 顔情報
     */
    private fun faceMultiAuth(faceItem: FaceItemModel) {
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
                img = BitmapUtils.toBase64Jpeg(faceItem.faceBitmap) ?: ""
            )

            // soundService?.playAuthStart()
            _sound.postValue(SoundId.AUTH_START)

            authJob = faceAuthUseCase(model).onEach { result ->
                when (result) {
                    is FaceAuthResponse.Success -> {
                        Timber.d("顔認証 ${result.response}")

                        // soundService?.playAuthSuccess()
                        _sound.postValue(SoundId.AUTH_SUCCESS)
                        _faceAuthState.postValue(
                            CameraScreenState(
                                request = result.request,
                                response = result.response
                            )
                        )
                    }

                    is FaceAuthResponse.Failure -> {
                        Timber.w("顔認証 ${result.error}")

                        // soundService?.playAuthError()
                        _sound.postValue(SoundId.AUTH_ERROR)
                        _faceAuthState.postValue(
                            CameraScreenState(
                                request = result.request,
                                error = result.error
                            )
                        )
                    }

                    is FaceAuthResponse.Loading -> {
                        Timber.d("顔認証 読み込み中.....")
                        _faceAuthState.postValue(
                            CameraScreenState(
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
    private val _cardAuthState = MutableLiveData<CameraScreenState>()

    /** カード認証状態 */
    val cardAuthState: LiveData<CameraScreenState> = _cardAuthState

    /**
     * カード認証を開始
     * @param authCode 認証コード
     */
    private fun cardAuth(authCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (authFlag.getAndSet(true)) {
                return@launch
            }

            Timber.d("  カード認証 start isActive=${authJob?.isActive} isCompleted=${authJob?.isCompleted}")

            val model = AppRequestModel(
                card = authCode
            )


            if (authJob?.isActive == true) {
                return@launch
            }

            // soundService?.playAuthStart()
            _sound.postValue(SoundId.AUTH_START)


            authJob = cardAuthUseCase(model).onEach { result ->
                when (result) {
                    is FaceAuthResponse.Success -> {
                        Timber.d("  カード認証 ${result.response}")

                        // soundService?.playAuthSuccess()
                        _sound.postValue(SoundId.AUTH_SUCCESS)
                        _cardAuthState.postValue(
                            CameraScreenState(
                                request = result.request,
                                response = result.response
                            )
                        )
                    }

                    is FaceAuthResponse.Failure -> {
                        Timber.w("  カード認証 ${result.error}")

                        // soundService?.playAuthError()
                        _sound.postValue(SoundId.AUTH_ERROR)
                        _cardAuthState.postValue(
                            CameraScreenState(
                                request = result.request,
                                error = result.error
                            )
                        )
                    }

                    is FaceAuthResponse.Loading -> {
                        Timber.d("  カード認証 読み込み中.....")
                        _cardAuthState.postValue(
                            CameraScreenState(
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
     * 多要素カード認証を開始
     * @param authCode 認証コード
     */
    private fun cardMultiAuth(authCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (authFlag.getAndSet(true)) {
                return@launch
            }

            Timber.d("  カード認証 start isActive=${authJob?.isActive} isCompleted=${authJob?.isCompleted}")

            val model = AppRequestModel(
                card = authCode
            )


            if (authJob?.isActive == true) {
                return@launch
            }

            // soundService?.playAuthStart()
            _sound.postValue(SoundId.AUTH_START)


            authJob = cardAuthUseCase(model).onEach { result ->
                when (result) {
                    is FaceAuthResponse.Success -> {
                        Timber.d("  カード認証 ${result.response}")

                        // soundService?.playAuthSuccess()
                        _sound.postValue(SoundId.AUTH_SUCCESS)
                        _cardAuthState.postValue(
                            CameraScreenState(
                                request = result.request,
                                response = result.response
                            )
                        )
                    }

                    is FaceAuthResponse.Failure -> {
                        Timber.w("  カード認証 ${result.error}")

                        // soundService?.playAuthError()
                        _sound.postValue(SoundId.AUTH_ERROR)
                        _cardAuthState.postValue(
                            CameraScreenState(
                                request = result.request,
                                error = result.error
                            )
                        )
                    }

                    is FaceAuthResponse.Loading -> {
                        Timber.d("  カード認証 読み込み中.....")
                        _cardAuthState.postValue(
                            CameraScreenState(
                                isLoading = true
                            )
                        )
                    }
                }
            }.launchIn(this)
            authJob?.join()

            // _authWaitProgressbar.postValue(true)
            // delay(10_000L)
            // _authWaitProgressbar.postValue(false)

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

    fun updateBeforeMultiAuthState(state: MultiAuthState) {
        when (state) {
            MultiAuthState.Before -> {
                if (setting.multiAuthType == MultiAuthType.CARD_FACE) {
                    isFaceAuthState.set(false)
                    isCardAuthState.set(true)
                }
                if (setting.multiAuthType == MultiAuthType.QR_FACE) {
                    isFaceAuthState.set(false)
                    isQrAuthState.set(true)
                }
            }

            MultiAuthState.After -> {
                if (setting.multiAuthType == MultiAuthType.CARD_FACE) {
                    isCardAuthState.set(false)
                    isFaceAuthState.set(true)
                }
                if (setting.multiAuthType == MultiAuthType.QR_FACE) {
                    isQrAuthState.set(false)
                    isFaceAuthState.set(true)
                }
            }
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


    // val activeBlocker = ActiveBlocker()
    // val logUploadLifecycleObserver: LifecycleObserver = object : DefaultLifecycleObserver {
    //     override fun onStart(owner: LifecycleOwner) {
    //         // activeBlocker.activate()
    //     }
    //
    //     override fun onStop(owner: LifecycleOwner) {
    //         // activeBlocker.deactivate()
    //     }
    // }

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
    private fun logUpload(fileName: String, file: File) {
        val log = MultipartBody.Part.createFormData(
            "LogFile", fileName, file.asRequestBody("text/plain".toMediaType())
        )

        // runCatching {
            logUploadUseCase(log).onEach { result ->
                when (result) {
                    is DeviceResponse.Success -> {
                        // Timber.d("${result.res}")
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
            }.launchIn(viewModelScope)
        // }.onSuccess {
        //     Timber.d("onSuccess")
        // }.onFailure {
        //     Timber.d("onFailure")
        // }


        // tomorrow1 { data ->
        //     Timber.d("明日の天気1: $data")
        // }
        //
        // val data = tomorrow2()
        // Timber.d("明日の天気2: $data")
        //
        // val result = withTimeoutOrNull(1000) {
        //     val data3 = tomorrow2()
        //     Timber.d("明日の天気3: $data")
        // }
        // Timber.d("明日の天気3: $result")


    }


    // val dispatcher1 = Executors.newFixedThreadPool(3).asCoroutineDispatcher()
    // val scope1 = CoroutineScope(dispatcher1)
    // val dispatcher2 = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    // val dispatcher3 = Executors.newCachedThreadPool().asCoroutineDispatcher()


    // val xxxJob = Job()
    // val xxxScope = CoroutineScope(xxxJob + Dispatchers.Main)
    // fun xxx() {
    //     xxxScope.launch {
    //         //
    //     }
    // }

    // fun xxxStop() {
    //     xxxJob.cancel()
    // }

    // fun tomorrow1(callback: (String) -> Unit): Unit {
    //     // 2秒後に処理を呼び出すようタイマーを設定する。
    //     // callback 処理はメインスレッドで行われるが、それまでの間スレッドは解放される
    //     val handler = Handler(Looper.getMainLooper())
    //     handler.postDelayed({ callback("晴れ") }, 5000)
    // }

    // suspend fun tomorrow2(): String = suspendCancellableCoroutine { c ->
    //     val callback = Runnable {
    //         // 結果を c に渡す。キャンセルされている場合は何もしない
    //         if (c.isActive) {
    //             c.resume("晴れ")
    //         }
    //     }
    //     val handler = Handler(Looper.getMainLooper())
    //     // coroutine のキャンセルが起きた時に Handler 側もキャンセルする
    //     c.invokeOnCancellation { handler.removeCallbacks(callback) }
    //
    //     // 2秒後に処理を呼び出すようタイマーを設定する。
    //     // callback 処理はメインスレッドで行われるが、それまでの間スレッドは解放される
    //     handler.postDelayed(callback, 5000)
    // }

    // suspend fun syncConnect() : Boolean {
    //     return suspendCoroutine { continuation ->
    //         val callback = object: MyConnectionCallback {
    //             override fun onConnected() {
    //                 continuation.resume(true) // ここでsyncConnect()をreturnする
    //             }
    //             override fun onError(error: Error) {
    //                 Log.e(LOG_TAG, this.javaClass.simpleName + ":onError:" + error.message);
    //                 continuation.resume(false) // ここでsyncConnect()をreturnする
    //             }
    //         }
    //         MyConnectionService.connect(callback)
    //         return@suspendCoroutine // resumeが呼ばれるまで待つ
    //     }
    // }


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
                val result = networkService.checkNetworkAvailable()
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
        networkService.getIpAddress { ipAddress ->
            _ipAddress.postValue(ipAddress)
        }
    }


    /** 時刻ズレチェック */
    private val _timeOff = MutableLiveData<Boolean>()

    /** 時刻ズレチェック */
    val timeOff: LiveData<Boolean>
        get() = _timeOff

    /**
     * 時刻ズレチェックを開始
     * @param delayTime 監視間隔
     */
    fun startCheckTimeOff(delayTime: Long = 300_000L) {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                Timber.i("時刻チェック start (${getThreadName()})")
                _timeOff.postValue(NtpService.isTimeSync())
                Timber.i("時刻チェック end (${getThreadName()})")

                delay(delayTime)
            }
        }
    }


    /** 位置情報 */
    private val _location = MutableLiveData<Location>()

    /** 位置情報 */
    val location: LiveData<Location>
        get() = _location

    /**
     * 位置情報を設定
     * @param location 位置情報
     */
    fun setLocation(location: Location) {
        _location.postValue(location)
    }


    /** 効果音 */
    private val _sound = MutableLiveData<SoundId>()

    /** 効果音 */
    val sound: LiveData<SoundId>
        get() = _sound


    /**
     * スレッド名を取得
     */
    private fun getThreadName(): String = Thread.currentThread().name
}
