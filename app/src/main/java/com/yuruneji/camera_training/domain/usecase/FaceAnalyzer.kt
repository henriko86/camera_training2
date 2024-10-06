package com.yuruneji.camera_training.domain.usecase

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.yuruneji.camera_training.common.CommonUtil
import com.yuruneji.camera_training.domain.model.FaceItem
import timber.log.Timber

/**
 * @author toru
 * @version 1.0
 */
class FaceAnalyzer(
    val onFaceDetect: (Int, Int, List<FaceItem>) -> Unit
) : ImageAnalysis.Analyzer {

    private var isShutdown = false

    private val opts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        // .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        // .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    private val detector: FaceDetector = FaceDetection.getClient(opts)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isShutdown) {
            imageProxy.close()
            return
        }

        imageProxy.image?.let { mediaImage ->
            val rotation = imageProxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(mediaImage, rotation)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val bmp: Bitmap = CommonUtil.flipBitmap(imageProxy.toBitmap(), rotation)
                        val faceList = mutableListOf<FaceItem>()
                        for (face in faces) {
                            val rect = face.boundingBox
                            val rect2 = Rect().also { // 左右反転（フロントカメラ対応）
                                it.top = rect.top
                                it.bottom = rect.bottom
                                it.left = bmp.width - rect.right
                                it.right = bmp.width - rect.left
                            }
                            val faceBitmap = CommonUtil.faceClipping(bmp, rect2)

                            faceList.add(FaceItem(faceBitmap, rect, face))
                        }

                        onFaceDetect(image.width, image.height, faceList)
                    } else {
                        onFaceDetect(0, 0, mutableListOf())
                    }
                }.addOnFailureListener { e ->
                    Timber.e(e, e.message)
                }.addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    fun close() {
        isShutdown = true
        detector.close()
    }
}
