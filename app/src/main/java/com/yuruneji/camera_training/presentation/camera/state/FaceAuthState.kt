package com.yuruneji.camera_training.presentation.camera.state

import com.yuruneji.camera_training.domain.model.AppResponseModel

/**
 * @author toru
 * @version 1.0
 */
data class FaceAuthState(
    /**  */
    val isLoading: Boolean = false,
    /**  */
    val faceAuth: AppResponseModel? = null,
    /**  */
    val error: Throwable? = null,
)
