package com.yuruneji.cameratraining2.domain.model

import android.graphics.Bitmap

/**
 * @author toru
 * @version 1.0
 */
data class CameraData(
    val cameraId: String,
    val width: Int,
    val height: Int,
    val bitmap: Bitmap
)
