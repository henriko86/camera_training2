package com.yuruneji.camera_training2.data.repository

import com.yuruneji.camera_training2.data.remote.AppRequest
import com.yuruneji.camera_training2.data.remote.AppResponse
import com.yuruneji.camera_training2.data.remote.AppService
import com.yuruneji.camera_training2.domain.repository.AppRepository
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

    override suspend fun log(log: MultipartBody.Part): AppResponse {
        return api.log(log)
    }
}
