package com.yuruneji.camera_training.data.repository

import com.yuruneji.camera_training.data.remote.AppRequest
import com.yuruneji.camera_training.data.remote.AppResponse
import com.yuruneji.camera_training.data.remote.AppService
import com.yuruneji.camera_training.domain.repository.AppRepository
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class AppRepositoryImpl @Inject constructor(
    private val api: AppService
) : AppRepository {
    override suspend fun faceAuth(request: AppRequest): AppResponse {
        return api.faceAuth(request)
    }

    override suspend fun cardAuth(request: AppRequest): AppResponse {
        return api.cardAuth(request)
    }

    override suspend fun log(log: MultipartBody.Part): AppResponse {
        return api.log(log)
    }
}
