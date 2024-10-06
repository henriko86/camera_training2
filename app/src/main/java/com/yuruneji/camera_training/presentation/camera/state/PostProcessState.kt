package com.yuruneji.camera_training.presentation.camera.state

import com.yuruneji.camera_training.domain.model.AppResponseModel

/**
 * @author toru
 * @version 1.0
 */
data class PostProcessState(
    /**  */
    val isLoading: Boolean = false,
    /**  */
    val faceAuthList: List<AppResponseModel> = emptyList(),
    /**  */
    val error: Throwable? = null,
)
