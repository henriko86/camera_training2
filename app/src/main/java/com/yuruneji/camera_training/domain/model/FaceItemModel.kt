package com.yuruneji.camera_training.domain.model

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.face.Face

/**
 * 顔情報
 * @author toru
 * @version 1.0
 */
data class FaceItemModel(
    val faceBitmap: Bitmap,
    val faceRect: Rect,
    val face: Face
)
