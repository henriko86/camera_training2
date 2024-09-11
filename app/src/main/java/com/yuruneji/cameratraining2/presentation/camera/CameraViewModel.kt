package com.yuruneji.cameratraining2.presentation.camera

import android.content.Context
import android.graphics.PixelFormat
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.common.NetworkResponse
import com.yuruneji.cameratraining2.domain.usecase.FaceAnalyzer
import com.yuruneji.cameratraining2.domain.usecase.LogUseCase
import com.yuruneji.cameratraining2.domain.usecase.TestWebServer
import com.yuruneji.cameratraining2.presentation.home.view.DrawFaceView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
    private val logUseCase: LogUseCase
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

        ContextCompat.getDrawable(context, R.drawable.face_rect)?.let { drawable ->
            Timber.i("${previewView.width}, ${previewView.height}")

            // 顔枠表示
            drawFaceView = DrawFaceView(
                surfaceView = surfaceView,
                drawable = drawable
            )
        }

        // cameraManager.cameraIdList.forEach { id ->
        //     Timber.i("カメラID:${id}")
        // }

        // surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
        // // surfaceView.holder.addCallback(surfaceHolderCallback)
        // surfaceView.setZOrderOnTop(true)


        // val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        // val rotation = windowManager!!.defaultDisplay.rotation
        // when (rotation) {
        //     Surface.ROTATION_0 -> {}
        //     Surface.ROTATION_90 -> {}
        //     Surface.ROTATION_180 -> {}
        //     Surface.ROTATION_270 -> {}
        //     else -> {}
        // }
        // cameraManager.getCameraCharacteristics()


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

    private var testWebServer: TestWebServer? = null
    private val port = 8888
    private val callback = object : TestWebServer.Callback {
        override fun onConnect(keyA: String, keyB: String) {
            Timber.d("onConnect keyA: $keyA, keyB: $keyB")
        }
    }

    fun startWebServer() {
        testWebServer = TestWebServer(port, callback)
        testWebServer?.start()
    }

    fun stopWebServer() {
        testWebServer?.stop()
    }

    private fun getThreadName(): String = Thread.currentThread().name

}
