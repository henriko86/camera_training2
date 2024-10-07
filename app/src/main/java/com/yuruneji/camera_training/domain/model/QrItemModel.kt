package com.yuruneji.camera_training.domain.model

import android.graphics.Rect
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * QRコード情報
 * @author toru
 * @version 1.0
 */
data class QrItemModel(
    val rect: Rect,
    val barcode: Barcode
)
