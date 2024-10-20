package com.yuruneji.camera_training.presentation.camera.state

import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.AppResponseModel

/**
 * @author toru
 * @version 1.0
 */
data class CameraScreenState(
    /**  */
    val isLoading: Boolean = false,
    /**  */
    val request: AppRequestModel? = null,
    /**  */
    val response: AppResponseModel? = null,
    /**  */
    val error: Throwable? = null,
)
