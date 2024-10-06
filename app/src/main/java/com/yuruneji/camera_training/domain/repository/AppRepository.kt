package com.yuruneji.camera_training.domain.repository

import com.yuruneji.camera_training.data.remote.AppRequest
import com.yuruneji.camera_training.data.remote.AppResponse
import okhttp3.MultipartBody

/**
 * @author toru
 * @version 1.0
 */
interface AppRepository {
    suspend fun faceAuth(request: AppRequest): AppResponse
    suspend fun cardAuth(request: AppRequest): AppResponse
    suspend fun log(log: MultipartBody.Part): AppResponse
}
