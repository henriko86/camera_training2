package com.yuruneji.camera_training.domain.model

/**
 * 顔情報抽出
 * FaceAnalyzerで使用
 * @author toru
 * @version 1.0
 */
data class FaceDetect(
    val width: Int = 0,
    val height: Int = 0,
    val faceList: List<FaceDetectDetail> = emptyList()
)
