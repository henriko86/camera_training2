package com.yuruneji.cameratraining2.presentation.home.state

/**
 * @author toru
 * @version 1.0
 */
data class SettingState(
    /** 認証タイプ */
    val authType: Int,
    /** 使用カメラ */
    val lensFacing: Int
)
