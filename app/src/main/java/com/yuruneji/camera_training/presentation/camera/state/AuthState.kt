package com.yuruneji.camera_training.presentation.camera.state

import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.AppResponseModel

/**
 * @author toru
 * @version 1.0
 */
data class AuthState(
    /**  */
    val isLoading: Boolean = false,
    /**  */
    val req: AppRequestModel? = null,
    /**  */
    val resp: AppResponseModel? = null,
    /**  */
    val error: Throwable? = null,
)
