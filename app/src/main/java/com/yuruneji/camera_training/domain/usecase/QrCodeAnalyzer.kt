package com.yuruneji.camera_training.domain.usecase

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.yuruneji.camera_training.domain.model.QrItemModel
import timber.log.Timber

/**
 * @author toru
 * @version 1.0
 */
class QrCodeAnalyzer(
    val onQrCodeDetect: (List<QrItemModel>) -> Unit
) : ImageAnalysis.Analyzer {

    private var isShutdown = false

    private val opts = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(opts)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isShutdown) {
            imageProxy.close()
            return
        }

        imageProxy.image?.let { mediaImage ->
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barCodes ->
                    if (barCodes.isNotEmpty()) {
                        val qrList = mutableListOf<QrItemModel>()
                        for (barCode in barCodes) {
                            val rect = barCode.boundingBox
                            rect?.let {
                                qrList.add(QrItemModel(rect, barCode))
                            }
                        }

                        onQrCodeDetect(qrList)
                    } else {
                        onQrCodeDetect(mutableListOf())
                    }
                }
                .addOnFailureListener {
                    Timber.e(it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    fun close() {
        isShutdown = true
        scanner.close()
    }
}
