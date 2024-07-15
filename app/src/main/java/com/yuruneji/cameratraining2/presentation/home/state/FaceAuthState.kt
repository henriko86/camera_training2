package com.yuruneji.cameratraining2.presentation.home.state

import com.yuruneji.cameratraining2.domain.model.FaceAuthInfo

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
