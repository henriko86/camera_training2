package com.yuruneji.camera_training.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @author toru
 * @version 1.0
 */
interface AppService {
    @POST("json/face")
    suspend fun faceAuth(@Body request: AppRequest): AppResponse

    @POST("json/card")
    suspend fun cardAuth(@Body request: AppRequest): AppResponse

    @POST("log")
    @Multipart
    suspend fun log(@Part log: MultipartBody.Part): AppResponse
}
