package com.yuruneji.cameratraining2.domain.usecase

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.yuruneji.cameratraining2.common.CommonUtil
import com.yuruneji.cameratraining2.domain.model.FaceDetect
import com.yuruneji.cameratraining2.domain.model.FaceDetectDetail
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author toru
 * @version 1.0
 */
class FaceAnalyzer(
    val faceDetectCallback: (FaceDetect) -> Unit,
) : ImageAnalysis.Analyzer {

    // private val _faceDetect = MutableStateFlow(FaceDetect())
    // val faceDetect: StateFlow<FaceDetect> = _faceDetect

    private var isShutdown = false

    private val opts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        // .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        // .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    private val detector: FaceDetector = FaceDetection.getClient(opts)

    private lateinit var handler1: Handler
    private lateinit var handler2: Handler

    private val executor1: ExecutorService = Executors.newSingleThreadExecutor()
    private val executor2: ExecutorService = Executors.newFixedThreadPool(3)
    private val executor3: ExecutorService = Executors.newCachedThreadPool()

    private val isTask: AtomicBoolean = AtomicBoolean(false)

    init {

        var thread = HandlerThread("hoge1")
        thread.start()
        handler1 = Handler(thread.looper)

        thread = HandlerThread("hoge2")
        thread.start()
        handler2 = Handler(thread.looper)

    }

    // companion object {
    //     // private const val TAG = "@Sample"
    //
    //     private val opts = FaceDetectorOptions.Builder()
    //         //            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
    //         //            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
    //         //            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
    //         .build()
    // }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isShutdown) {
            imageProxy.close()
            return
        }



        imageProxy.image?.let { mediaImage ->
            val rotation = imageProxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(mediaImage, rotation)
            val bmp: Bitmap = CommonUtil.flipBitmap(imageProxy.toBitmap(), rotation)



            detector.process(image)
                .addOnSuccessListener { faces ->
                    // Log.d(TAG, "addOnSuccessListener [${getThreadName()}]")
                    if (isTask.getAndSet(true)) {
                        Timber.i("  スキップ: ${isTask.get()}")
                        return@addOnSuccessListener
                    }


                    val faceList = mutableListOf<FaceDetectDetail>()

                    for (face in faces) {
                        val rect = face.boundingBox

                        val x = ((rect.right - rect.left) / 2) + rect.left
                        val y = ((rect.bottom - rect.top) / 2) + rect.top
                        val point = Point(x, y)

                        val faceRect2 = Rect().also {
                            it.top = rect.top
                            it.bottom = rect.bottom
                            it.left = bmp.width - rect.right
                            it.right = bmp.width - rect.left
                        }

                        val faceBitmap = CommonUtil.faceClipping(bmp, faceRect2)
                        faceList.add(
                            FaceDetectDetail(
                                faceBitmap = faceBitmap, faceRect = rect, center = point
                            )
                        )

                        // bmp.recycle()
                    }

                    faceDetectCallback(
                        FaceDetect(
                            width = bmp.width,
                            height = bmp.height,
                            faceList = faceList,
                        )
                    )
                    // _faceDetect.value = FaceDetect(
                    //     width = bmp.width,
                    //     height = bmp.height,
                    //     faceList = faceList,
                    // )


                }.addOnFailureListener { e ->
                    Timber.e(e, e.message)
                    isTask.set(false)
                }.addOnCompleteListener {
                    mediaImage.close()
                    imageProxy.close()
                    // bmp.recycle()
                    isTask.set(false)
                }
        }
    }

    fun close() {
        isShutdown = true
        detector.close()
    }

}
