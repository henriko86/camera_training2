package com.yuruneji.cameratraining2.presentation.camera2

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.domain.model.CameraData
import com.yuruneji.cameratraining2.domain.usecase.Camera2Controller
import com.yuruneji.cameratraining2.presentation.home.view.DrawFaceView
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class Camera2ViewModel @Inject constructor(
    //
) : ViewModel() {



    private var cameraDevice: CameraDevice? = null
    private var surfaceTexture: SurfaceTexture? = null

    /** 顔枠表示 */
    private var drawFaceView: DrawFaceView? = null

    private var camera2Controller: Camera2Controller? = null
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

    fun startCamera(context: Context, textureView: TextureView, surfaceView: SurfaceView) {
        Timber.d("startCamera()")

        surfaceView.holder.addCallback(surfaceHolderCallback)
        surfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
        surfaceView.setZOrderOnTop(true)

        ContextCompat.getDrawable(context, R.drawable.face_rect)?.let { drawable ->
            Timber.i("${textureView.width}, ${textureView.height}")

            // 顔枠表示
            drawFaceView = DrawFaceView(
                surfaceView = surfaceView,
                drawable = drawable
            )
        }

        val cameraId = "1"

        camera2Controller = Camera2Controller(
            context,
            textureView,
            640,
            480,
            cameraId,
            object : Camera2Controller.Callback {
                override fun onComplete(cameraData: CameraData) {
                    Timber.d("onComplete()")

                    // drawFaceView?.drawFace(
                    //     previewView.matrix,
                    //     previewView.width,
                    //     previewView.height,
                    //     faceDetect.width,
                    //     faceDetect.height,
                    //     faceDetect.faceList
                    // )
                }

                override fun onFailure(throwable: Throwable) {
                    Timber.e(throwable)
                }
            })
        camera2Controller?.onResume()
    }

    fun stopCamera() {
        Timber.d("stopCamera()")
        camera2Controller?.onPause()
    }
}
