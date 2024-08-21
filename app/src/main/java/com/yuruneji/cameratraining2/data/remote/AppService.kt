package com.yuruneji.cameratraining2.data.remote

import com.google.android.datatransport.cct.internal.LogRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @author toru
 * @version 1.0
 */
interface AppService {
    @POST("json")
    suspend fun faceAuth(@Body request: AppRequest): AppResponse

    @POST("log")
    @Multipart
    suspend fun log(@Part log: MultipartBody.Part): AppResponse
}
