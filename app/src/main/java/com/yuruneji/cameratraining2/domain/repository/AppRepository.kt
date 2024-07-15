package com.yuruneji.cameratraining2.domain.repository

import com.yuruneji.cameratraining2.data.remote.AppRequest
import com.yuruneji.cameratraining2.data.remote.AppResponse

/**
 * @author toru
 * @version 1.0
 */
interface AppRepository {
    suspend fun faceAuth(request: AppRequest): AppResponse
}
