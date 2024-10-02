package com.yuruneji.camera_training2.presentation.camera

import android.app.Activity
import android.content.Context
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
import com.yuruneji.camera_training2.R
import com.yuruneji.camera_training2.common.NetworkResponse
import com.yuruneji.camera_training2.data.remote.AppRequest
import com.yuruneji.camera_training2.domain.usecase.CardFaceAuthUseCase
import com.yuruneji.camera_training2.domain.usecase.FaceAnalyzer
import com.yuruneji.camera_training2.domain.usecase.FaceAuthUseCase
import com.yuruneji.camera_training2.domain.usecase.LocationSensor
import com.yuruneji.camera_training2.domain.usecase.LogUseCase
import com.yuruneji.camera_training2.domain.usecase.NetworkSensor
import com.yuruneji.camera_training2.domain.usecase.TestWebServer
import com.yuruneji.camera_training2.domain.usecase.TimeSensor
import com.yuruneji.camera_training2.presentation.camera.state.AuthStateEnum
import com.yuruneji.camera_training2.presentation.home.view.DrawFaceView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val logUseCase: LogUseCase,
    private val networkSensor: NetworkSensor,
    private val cardAuthUseCase: FaceAuthUseCase,
    private val faceAuthUseCase: FaceAuthUseCase,
    private val cardFaceAuthUseCase: CardFaceAuthUseCase,
) : ViewModel() {
    //

    /** 顔枠表示 */
    private var drawFaceView: DrawFaceView? = null

    private var camera: Camera? = null
    private var faceAnalyzer: FaceAnalyzer? = null
    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            Timber.i("surfaceCreated()")
        }

        override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            Timber.i("surfaceChanged()")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Timber.i("surfaceDestroyed()")
        }
    }

    fun startCamera(
        context: Context,
        owner: LifecycleOwner,
        previewView: PreviewView,
        surfaceView: SurfaceView
    ) {
        Timber.d("startCamera()")

        surfaceView.holder.addCallback(surfaceHolderCallback)
        surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
        surfaceView.setZOrderOnTop(true)

        // 顔枠表示
        ContextCompat.getDrawable(context, R.drawable.face_rect)?.let { drawable ->
            drawFaceView = DrawFaceView(
                surfaceView = surfaceView,
                drawable = drawable
            )
        }

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

            val builder = ImageAnalysis.Builder()
            val imageAnalysis = builder
                .setOutputImageRotationEnabled(true)
                // .setTargetRotation(rotation)
                .build()

            // TODO:
            // faceAnalyzer = FaceAnalyzer()
            faceAnalyzer = FaceAnalyzer { faceDetect ->
                drawFaceView?.drawFace(
                    previewView.matrix,
                    previewView.width,
                    previewView.height,
                    faceDetect.width,
                    faceDetect.height,
                    faceDetect.faceList
                )

                // faceDetect.faceList.forEach { faceDetectDetail ->
                //     lifecycleScope.launch {
                //         withContext(Dispatchers.IO) {
                //             viewModel.faceAuth(faceDetectDetail)
                //         }
                //     }
                // }
            }
            // lifecycleScope.launch {
            //     faceAnalyzer.faceDetect.collect { faceDetect ->
            //         drawFaceView?.drawFace(
            //             previewView.matrix,
            //             previewView.width,
            //             previewView.height,
            //             faceDetect.width,
            //             faceDetect.height,
            //             faceDetect.faceList
            //         )
            //
            //         faceDetect.faceList.forEach { faceDetectDetail ->
            //             lifecycleScope.launch {
            //                 withContext(Dispatchers.IO) {
            //                     viewModel.faceAuth(faceDetectDetail)
            //                 }
            //             }
            //         }
            //     }
            // }


            // cameraExecutor = Executors.newSingleThreadExecutor()
            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), faceAnalyzer!!)

            try {
                // バインドされているカメラを解除
                cameraProvider.unbindAll()
                // カメラをライフサイクルにバインド
                camera = cameraProvider.bindToLifecycle(
                    owner as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Timber.e(exc, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun stopCamera() {
        Timber.d("stopCamera()")

        faceAnalyzer?.close()
    }

    private val _cardFaceAuthState = MutableLiveData<AuthStateEnum>()
    val cardFaceAuthState: MutableLiveData<AuthStateEnum> = _cardFaceAuthState
    private var cardFaceAuthJob: Job? = null

    fun startCardFaceAuth() {
        Timber.d("カード&顔認証 start")
        viewModelScope.launch(Dispatchers.IO) {
            val result = withTimeoutOrNull(10_000L) {
                cardFaceAuthJob = launch {
                    _cardFaceAuthState.postValue(AuthStateEnum.LOADING)

                    startCardAuth()

                    repeat(1000) { i ->
                        Timber.d("    カード&顔認証 sleeping $i ...")
                        delay(1000L)
                    }
                }
                cardFaceAuthJob?.join()

                Timber.d("カード&顔認証 end")

                "Done"
            }

            if (result == null) {
                _cardFaceAuthState.postValue(AuthStateEnum.FAIL)
            } else {
                _cardFaceAuthState.postValue(AuthStateEnum.SUCCESS)
            }

            Timber.d("カード&顔認証 result: $result")
        }
    }

    suspend fun cancelCardFaceAuth() {
        cardFaceAuthJob?.cancelAndJoin()
    }


    private val _cardAuthState = MutableLiveData<AuthStateEnum>()
    val cardAuthState: MutableLiveData<AuthStateEnum> = _cardAuthState

    /** カード認証Job */
    private var cardAuthJob: Job? = null

    /**
     * カード認証を開始
     */
    fun startCardAuth() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("  カード認証 start")

            val request = AppRequest()
            val cardAuthJob = cardAuthUseCase(request).onEach { result ->
                when (result) {
                    is NetworkResponse.Success -> {
                        Timber.d("  カード認証 ${result.data}")
                        _cardAuthState.postValue(AuthStateEnum.SUCCESS)
                    }

                    is NetworkResponse.Failure -> {
                        Timber.w("  カード認証 ${result.error}")
                        _cardAuthState.postValue(AuthStateEnum.FAIL)
                    }

                    is NetworkResponse.Loading -> {
                        Timber.d("  カード認証 読み込み中.....")
                        _cardAuthState.postValue(AuthStateEnum.LOADING)
                    }
                }
            }.launchIn(this)
            cardAuthJob.join()

            Timber.d("  カード認証 end")
        }
    }

    /**
     * カード認証をキャンセル
     */
    suspend fun cancelCardAuth() {
        cardAuthJob?.cancelAndJoin()
    }


    private val _faceAuthState = MutableLiveData<AuthStateEnum>()
    val faceAuthState: MutableLiveData<AuthStateEnum> = _faceAuthState

    /** 顔認証Job */
    private var faceAuthJob: Job? = null

    /**
     * 顔認証を開始
     */
    fun startFaceAuth() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("  顔認証 start")
            val request = AppRequest()
            faceAuthJob = faceAuthUseCase(request).onEach { result ->
                when (result) {
                    is NetworkResponse.Success -> {
                        Timber.d("  顔認証 ${result.data}")
                        _faceAuthState.postValue(AuthStateEnum.SUCCESS)
                    }

                    is NetworkResponse.Failure -> {
                        Timber.w("  顔認証 ${result.error}")
                        _faceAuthState.postValue(AuthStateEnum.FAIL)
                    }

                    is NetworkResponse.Loading -> {
                        Timber.d("  顔認証 読み込み中.....")
                        _faceAuthState.postValue(AuthStateEnum.LOADING)
                    }
                }
            }.launchIn(this)
            faceAuthJob?.join()

            Timber.d("  顔認証 end")
        }
    }

    /**
     * 顔認証をキャンセル
     */
    suspend fun cancelFaceAuth() {
        faceAuthJob?.cancelAndJoin()
    }


    fun getLogFile(): File {
        val file = File("hoge.log")
        // file.createNewFile()

        try {
            PrintWriter(BufferedWriter(FileWriter(file))).use { writer ->
                writer.println("hoge")
                writer.println("hoge")
            }
        } catch (e: IOException) {
            Timber.e(e)
        }

        return file
    }

    fun logUpload(fileName: String, file: File) {
        Timber.d("logUpload start")

        viewModelScope.launch(Dispatchers.IO) {
            val log = MultipartBody.Part.createFormData(
                "LogFile", fileName, file.asRequestBody("text/plain".toMediaType())
            )

            val job = logUseCase(log).onEach { result ->
                when (result) {
                    is NetworkResponse.Success -> {
                        Timber.d("${result.data}")
                        Timber.d("ログアップロード!!!! [" + getThreadName() + "]")
                    }

                    is NetworkResponse.Failure -> {
                        Timber.w("${result.error}")
                        Timber.d("ログアップロードエラー [" + getThreadName() + "]")
                    }

                    is NetworkResponse.Loading -> {
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


    private val _networkState = MutableLiveData<Boolean>()
    val networkState: MutableLiveData<Boolean> = _networkState
    private var networkSensorJob: Job? = null

    fun startNetworkSensor(delayTime: Long) = viewModelScope.launch(Dispatchers.Default) {
        networkSensorJob = launch {
            while (isActive) {
                Timber.i("ネットワーク状態 start (${getThreadName()})")
                _networkState.postValue(networkSensor.checkNetworkAvailable())
                Timber.i("ネットワーク状態 end (${getThreadName()})")

                // val str = "90123ABCabc"
                // Timber.d(str)
                // Timber.d(CommonUtil.string2Ascii(str).joinToString(":"))

                // Timber.d(CommonUtil.int2AsciiString(123, ":"))
                // Timber.d(CommonUtil.int2AsciiString(1, 4, ":"))
                // Timber.d(CommonUtil.string2AsciiString("ABCabc", ":"))

                // val str2 = CommonUtil.string2AsciiString(str)
                // Timber.d(CommonUtil.string2AsciiString(str2, ":"))

                delay(delayTime)
            }
        }
    }

    fun stopNetworkSensor() = viewModelScope.launch(Dispatchers.Default) {
        networkSensorJob?.cancelAndJoin()
    }


    /** IPアドレス */
    private val _ipAddress: MutableLiveData<String> = MutableLiveData<String>()

    /** IPアドレス */
    val ipAddress: LiveData<String> = _ipAddress

    /**
     * IPアドレスを取得
     */
    fun getIpAddress(context: Context) {
        networkSensor.getIpAddress(context) { ipAddress ->
            _ipAddress.postValue(ipAddress)
        }
    }


    private val timeSensor = TimeSensor()
    private val _timeCheck: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val timeCheck: LiveData<Boolean> = _timeCheck
    private var timeCheckJob: Job? = null

    /**
     * 時刻チェックを開始
     */
    fun startTimeCheck(delayTime: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            timeCheckJob = launch {
                while (isActive) {
                    Timber.i("時刻チェック start (${getThreadName()})")
                    _timeCheck.postValue(timeSensor.checkTime())
                    Timber.i("時刻チェック end (${getThreadName()})")

                    delay(delayTime)
                }
            }
        }
    }

    fun isTimeCheck(): Boolean {
        return timeCheckJob?.isActive ?: false
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
    private val _location: MutableLiveData<Location> = MutableLiveData<Location>()

    /** 位置情報 */
    val location: LiveData<Location> = _location

    fun initLocationSensor(activity: Activity, owner: LifecycleOwner) {
        locationSensor = LocationSensor(activity)
        locationSensor.requestLocationPermission()

        locationSensor.location.observe(owner) {
            _location.postValue(it)
        }
    }

    /** 位置情報 */
    fun startLocationSensor(intervalTime: Long) {
        locationSensor.start(intervalTime)
    }

    fun stopLocationSensor() {
        locationSensor.stop()
    }


    /**
     * スレッド名を取得
     */
    private fun getThreadName(): String = Thread.currentThread().name
}
