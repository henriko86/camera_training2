package com.yuruneji.cameratraining2.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author toru
 * @version 1.0
 */
interface AppService {
    @POST("json")
    suspend fun faceAuth(@Body request: AppRequest): AppResponse
}
