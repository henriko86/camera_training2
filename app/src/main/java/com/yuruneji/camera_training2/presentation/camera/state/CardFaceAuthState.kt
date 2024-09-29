package com.yuruneji.camera_training2.presentation.camera.state

import com.yuruneji.camera_training2.domain.model.FaceAuthInfo

/**
 * @author toru
 * @version 1.0
 */
data class CardFaceAuthState(
    /**  */
    val isLoading: Boolean = false,
    /**  */
    val faceAuthList: List<FaceAuthInfo> = emptyList(),
    /**  */
    val error: Throwable? = null,
)

enum class AuthStateEnum{
    LOADING,
    SUCCESS,
    FAIL
}
