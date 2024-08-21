package com.yuruneji.cameratraining2.presentation.camera.state

import com.yuruneji.cameratraining2.domain.model.FaceAuthInfo

/**
 * @author toru
 * @version 1.0
 */
data class PostProcessState(
    /**  */
    val isLoading: Boolean = false,
    /**  */
    val faceAuthList: List<FaceAuthInfo> = emptyList(),
    /**  */
    val error: Throwable? = null,
)
