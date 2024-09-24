package com.yuruneji.camera_training2.domain.model

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect

/**
 * 顔情報抽出
 * FaceAnalyzerで使用
 * @author toru
 * @version 1.0
 */
data class FaceDetectDetail(
    val faceBitmap: Bitmap?,
    val faceRect: Rect,
    val center: Point
)
