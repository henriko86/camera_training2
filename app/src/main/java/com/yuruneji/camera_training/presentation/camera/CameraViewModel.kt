package com.yuruneji.camera_training.presentation.camera

import android.app.Activity
import android.content.Context
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.location.Location
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuruneji.camera_training.common.AuthResponse
import com.yuruneji.camera_training.common.LogUploadResponse
import com.yuruneji.camera_training.common.SoundManager
import com.yuruneji.camera_training.common.TimeService
import com.yuruneji.camera_training.common.toByteArray
import com.yuruneji.camera_training.data.local.preference.CameraPreferences
import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.FaceItemModel
import com.yuruneji.camera_training.domain.model.QrItemModel
import com.yuruneji.camera_training.domain.usecase.CardAuthUseCase
import com.yuruneji.camera_training.domain.usecase.FaceAnalyzer
import com.yuruneji.camera_training.domain.usecase.FaceAuthUseCase
import com.yuruneji.camera_training.domain.usecase.LocationSensor
import com.yuruneji.camera_training.domain.usecase.LogUploadUseCase
import com.yuruneji.camera_training.domain.usecase.NetworkSensor
import com.yuruneji.camera_training.domain.usecase.QrCodeAnalyzer
import com.yuruneji.camera_training.domain.usecase.TestWebServer
import com.yuruneji.camera_training.presentation.camera.state.AuthState
import com.yuruneji.camera_training.presentation.camera.view.DrawRectView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
    private val cameraPref: CameraPreferences,
    private val soundManager: SoundManager,
    private val networkSensor: NetworkSensor,
    private val faceAuthUseCase: FaceAuthUseCase,
    private val cardAuthUseCase: CardAuthUseCase,
    private val logUploadUseCase: LogUploadUseCase,
) : ViewModel() {


    /** 顔枠表示 */
    private var drawFaceView: DrawRectView? = null

    /**
     * 顔枠表示を初期化
     * @param context Context
     * @param sv      SurfaceView
     * @param matrix  プレビューMatrix
     * @param width   プレビュー幅
     * @param height  プレビュー高さ
     */
    fun initDrawFaceView(context: Context, sv: SurfaceView, matrix: Matrix, width: Int, height: Int) {
        sv.holder.addCallback(surfaceHolderCallback)
        sv.holder.setFormat(PixelFormat.TRANSLUCENT)
        sv.setZOrderOnTop(true)

        drawFaceView = DrawRectView(context, sv, matrix, width, height)
    }

    /**
     * 顔枠表示
     * @param width        画像幅
     * @param height       画像高さ
     * @param faceItemList 顔情報
     */
    private fun drawFace(width: Int, height: Int, faceItemList: List<FaceItemModel>) {
        drawFaceView?.draw(width, height, faceItemList.map { it.faceRect })
    }

    /** QR枠表示 */
    private var drawQrView: DrawRectView? = null

    /**
     * QR枠表示を初期化
     * @param context Context
     * @param sv      SurfaceView
     * @param matrix  プレビューMatrix
     * @param width   プレビュー幅
     * @param height  プレビュー高さ
     */
    fun initDrawQrView(context: Context, sv: SurfaceView, matrix: Matrix, width: Int, height: Int) {
        sv.holder.addCallback(surfaceHolderCallback)
        sv.holder.setFormat(PixelFormat.TRANSLUCENT)
        sv.setZOrderOnTop(true)

        drawQrView = DrawRectView(context, sv, matrix, width, height)
    }

    /**
     * QR枠表示
     * @param width      画像幅
     * @param height     画像高さ
     * @param qrItemList QR情報
     */
    private fun drawQr(width: Int, height: Int, qrItemList: List<QrItemModel>) {
        drawQrView?.draw(width, height, qrItemList.map { it.rect })
    }


    /** Camera */
    private var camera: Camera? = null

    /** 顔解析 */
    private var faceAnalyzer: FaceAnalyzer? = null

    /** QRコード解析 */
    private var qrCodeAnalyzer: QrCodeAnalyzer? = null

    /** SurfaceHolder Callback */
    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            Timber.i("surfaceCreated()")
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Timber.i("surfaceChanged()")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Timber.i("surfaceDestroyed()")
        }
    }


    // private val _cameraSettingModel = MutableLiveData<CameraSettingModel>()
    // val cameraSettingModel: LiveData<CameraSettingModel> = _cameraSettingModel

    /** 顔認証実行有無 */
    private val isFaceAuth = AtomicBoolean(true)

    /** カード認証実行有無 */
    private val isCardAuth = AtomicBoolean(true)

    /** QRコード認証実行有無 */
    private val isQrAuth = AtomicBoolean(true)


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


        // _cameraSettingState.postValue(cameraPref.convertCameraSettingState())

        // 顔認証
        val faceAnalyzer = FaceAnalyzer { width, height, faceItem ->
            faceAnalyze(width, height, faceItem)
        }
        val faceImageAnalysis = ImageAnalysis.Builder().setOutputImageRotationEnabled(true).build()
        faceImageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), faceAnalyzer)

        // QRコード認証
        val barCodeScanner = QrCodeAnalyzer { width, height, qrItem ->
            qrAnalyze(width, height, qrItem)
        }
        val qrImageAnalysis = ImageAnalysis.Builder().setOutputImageRotationEnabled(true).build()
        qrImageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), barCodeScanner)


        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            // ライフサイクルにバインドするために利用する
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()

            // PreviewのUseCase
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            cameraProvider.unbind(preview)

            // カメラを設定
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {
                // バインドされているカメラを解除
                cameraProvider.unbindAll()
                // カメラをライフサイクルにバインド
                camera = cameraProvider.bindToLifecycle(
                    owner,
                    cameraSelector,
                    preview,
                    faceImageAnalysis,
                    qrImageAnalysis
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
     * @param width
     * @param height
     * @param itemList
     */
    private fun faceAnalyze(width: Int, height: Int, itemList: List<FaceItemModel>) {
        if (cameraPref.faceAuth) {
            viewModelScope.launch(Dispatchers.Default) {
                drawFace(width, height, itemList)

                if (itemList.isNotEmpty()) {
                    faceAuth(itemList.first())
                }
            }
        }
    }

    /**
     * QRコード認証
     * @param width
     * @param height
     * @param itemList
     */
    private fun qrAnalyze(width: Int, height: Int, itemList: List<QrItemModel>) {
        if (cameraPref.qrAuth) {
            viewModelScope.launch(Dispatchers.Default) {
                drawQr(width, height, itemList)

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

            playAuthStartSound()

            authJob = faceAuthUseCase(model).onEach { result ->
                when (result) {
                    is AuthResponse.Success -> {
                        Timber.d("顔認証 ${result.resp}")

                        playAuthSuccessSound()
                        _faceAuthState.postValue(
                            AuthState(
                                req = result.req,
                                resp = result.resp
                            )
                        )
                    }

                    is AuthResponse.Failure -> {
                        Timber.w("顔認証 ${result.error}")

                        playAuthFailSound()
                        _faceAuthState.postValue(
                            AuthState(
                                req = result.req,
                                error = result.error
                            )
                        )
                    }

                    is AuthResponse.Loading -> {
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
                return@launch
            }

            Timber.d("  カード認証 start")

            val model = AppRequestModel(
                card = authCode
            )

            playAuthStartSound()

            authJob = cardAuthUseCase(model).onEach { result ->
                when (result) {
                    is AuthResponse.Success -> {
                        Timber.d("  カード認証 ${result.resp}")

                        playAuthSuccessSound()
                        _cardAuthState.postValue(
                            AuthState(
                                req = result.req,
                                resp = result.resp
                            )
                        )
                    }

                    is AuthResponse.Failure -> {
                        Timber.w("  カード認証 ${result.error}")

                        playAuthFailSound()
                        _cardAuthState.postValue(
                            AuthState(
                                req = result.req,
                                error = result.error
                            )
                        )
                    }

                    is AuthResponse.Loading -> {
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


    /** ログアップロード */
    private var logUploadJob: Job? = null

    /**
     * ログアップロードを開始
     * @param context Context
     */
    fun startLogUpload(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            logUploadJob = launch {
                val today = LocalDateTime.now()
                val fileName = "${today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.log"
                val logFile = File(context.filesDir, fileName)

                logUpload(fileName, logFile)

                delay(600_000)
            }
        }
    }

    /**
     * ログアップロードを停止
     */
    fun stopLogUpload() {
        viewModelScope.launch {
            logUploadJob?.cancelAndJoin()
        }
    }

    /**
     * ログアップロード
     * @param fileName ログファイル名
     * @param file     ログファイル
     */
    private fun logUpload(fileName: String, file: File) {
        Timber.d("logUpload start")

        viewModelScope.launch(Dispatchers.IO) {
            val log = MultipartBody.Part.createFormData(
                "LogFile", fileName, file.asRequestBody("text/plain".toMediaType())
            )

            val job = logUploadUseCase(log).onEach { result ->
                when (result) {
                    is LogUploadResponse.Success -> {
                        Timber.d("${result.data}")
                        Timber.d("ログアップロード!!!! [" + getThreadName() + "]")
                    }

                    is LogUploadResponse.Failure -> {
                        Timber.w("${result.error}")
                        Timber.d("ログアップロードエラー [" + getThreadName() + "]")
                    }

                    is LogUploadResponse.Loading -> {
                        Timber.d("ログアップロード 読み込み中..... [" + getThreadName() + "]")
                    }
                }
            }.launchIn(this)
            job.join()

            Timber.d("logUpload end")
        }
    }


    /** TestWebServer */
    private var testWebServer: TestWebServer? = null

    /** TestWebServer ポート */
    private val port = 8888

    /**
     * TestWebServerを開始
     */
    fun startWebServer() {
        testWebServer = TestWebServer(port, object : TestWebServer.Callback {
            override fun onConnect(keyA: String, keyB: String) {
                Timber.d("onConnect keyA: $keyA, keyB: $keyB")
            }
        })
        testWebServer?.start()
    }

    /**
     * TestWebServerを停止
     */
    fun stopWebServer() {
        testWebServer?.stop()
    }


    /** ネットワーク状態 */
    private val _networkState = MutableLiveData<Boolean>(false)

    /** ネットワーク状態 */
    val networkState: LiveData<Boolean> = _networkState

    /** ネットワーク状態監視Job */
    private var networkSensorJob: Job? = null

    /**
     * ネットワーク状態監視を開始
     * @param delayTime 監視間隔
     */
    fun startNetworkSensor(delayTime: Long = 10_000L) {
        viewModelScope.launch(Dispatchers.Default) {
            networkSensorJob = launch {
                while (isActive) {
                    val result = networkSensor.checkNetworkAvailable()
                    if (result != _networkState.value) {
                        _networkState.postValue(result)
                    }

                    delay(delayTime)
                }
            }
        }
    }

    /**
     * ネットワーク状態監視を停止
     */
    fun stopNetworkSensor() {
        viewModelScope.launch(Dispatchers.Default) {
            networkSensorJob?.cancelAndJoin()
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

    /** 時刻チェック */
    private var timeCheckJob: Job? = null

    /**
     * 時刻チェックを開始
     * @param delayTime 監視間隔
     */
    fun startTimeCheck(delayTime: Long = 300_000L) {
        viewModelScope.launch(Dispatchers.Default) {
            timeCheckJob = launch {
                while (isActive) {
                    Timber.i("時刻チェック start (${getThreadName()})")
                    _timeCheck.postValue(TimeService.isTimeSync())
                    Timber.i("時刻チェック end (${getThreadName()})")

                    delay(delayTime)
                }
            }
        }
    }

    /**
     * 時刻チェックを停止
     */
    fun stopTimeCheck() = viewModelScope.launch(Dispatchers.Default) {
        timeCheckJob?.cancelAndJoin()
    }


    /** 位置情報 */
    private lateinit var locationSensor: LocationSensor

    /** 位置情報 */
    private val _location = MutableLiveData<Location>()

    /** 位置情報 */
    val location: LiveData<Location> = _location

    /**
     * 位置情報を初期化
     * @param activity Activity
     * @param owner LifecycleOwner
     */
    fun initLocation(activity: Activity, owner: LifecycleOwner) {
        locationSensor = LocationSensor(activity)
        locationSensor.requestLocationPermission()

        locationSensor.location.observe(owner) {
            _location.postValue(it)
        }
    }

    /**
     * 位置情報取得処理を開始
     * @param intervalTime 位置情報取得間隔
     */
    fun startLocation(intervalTime: Long = 300_000L) {
        locationSensor.start(intervalTime)
    }

    /**
     * 位置情報取得処理を停止
     */
    fun stopLocation() {
        locationSensor.stop()
    }


    /**
     * 認証開始音を再生
     */
    private fun playAuthStartSound() {
        viewModelScope.launch {
            soundManager.start()
        }
    }

    /**
     * 認証成功音を再生
     */
    private fun playAuthSuccessSound() {
        viewModelScope.launch {
            soundManager.success()
        }
    }

    /**
     * 認証失敗音を再生
     */
    private fun playAuthFailSound() {
        viewModelScope.launch {
            soundManager.error()
        }
    }


    /**
     * スレッド名を取得
     */
    private fun getThreadName(): String = Thread.currentThread().name
}
