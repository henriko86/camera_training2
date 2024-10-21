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
import com.yuruneji.camera_training.common.BitmapUtils
import com.yuruneji.camera_training.domain.model.FaceItemModel
import timber.log.Timber

/**
 * @author toru
 * @version 1.0
 */
class FaceAnalyzer(
    val onFaceDetect: (List<FaceItemModel>) -> Unit
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
                        val bmp: Bitmap = BitmapUtils.flip(imageProxy.toBitmap(), rotation)
                        val faceList = mutableListOf<FaceItemModel>()
                        for (face in faces) {
                            val rect = face.boundingBox

                            val widthPadding = (rect.width() * 1.2) - rect.width().toDouble()
                            val heightPadding = (rect.height() * 1.2) - rect.height().toDouble()

                            val rect2 = Rect().also { // 左右反転（フロントカメラ対応）
                                it.top = rect.top - heightPadding.toInt()
                                it.bottom = rect.bottom + heightPadding.toInt()
                                it.left = (bmp.width - rect.right) - widthPadding.toInt()
                                it.right = (bmp.width - rect.left) + widthPadding.toInt()
                            }
                            val faceBitmap = BitmapUtils.crop(bmp, rect2)
                            faceBitmap?.let {
                                val faceBitmap2 = BitmapUtils.trim(faceBitmap, 100, 100)

                                val base64 = BitmapUtils.toBase64(faceBitmap2, Bitmap.CompressFormat.JPEG)
                                val bitmap3 = base64.let { BitmapUtils.toBitmap(base64) }

                                bitmap3?.let {
                                    faceList.add(FaceItemModel(bitmap3, rect, face))
                                }
                            }
                        }

                        onFaceDetect(faceList)
                    } else {
                        onFaceDetect(mutableListOf())
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
