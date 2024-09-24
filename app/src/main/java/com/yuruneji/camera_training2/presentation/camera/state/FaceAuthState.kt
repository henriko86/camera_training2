package com.yuruneji.camera_training2.presentation.camera.state

import com.yuruneji.camera_training2.domain.model.FaceAuthInfo

/**
 * @author toru
 * @version 1.0
 */
data class FaceAuthState(
    /**  */
    val isLoading: Boolean = false,
    /**  */
    val faceAuth: FaceAuthInfo? = null,
    /**  */
    val error: Throwable? = null,
)
