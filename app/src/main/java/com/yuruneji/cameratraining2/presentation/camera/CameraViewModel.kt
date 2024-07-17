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
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.domain.usecase.FaceAnalyzer
import com.yuruneji.cameratraining2.presentation.home.view.DrawFaceView
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
) : ViewModel() {
    //

    /** 顔枠表示 */
    private var drawFaceView: DrawFaceView? = null

    private var camera: Camera? = null
    private lateinit var faceAnalyzer: FaceAnalyzer
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

    fun startCamera(context: Context,owner: LifecycleOwner, previewView: PreviewView, surfaceView: SurfaceView) {
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
            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), faceAnalyzer)

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

        faceAnalyzer.close()
    }
}
