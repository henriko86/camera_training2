package com.yuruneji.camera_training.domain.usecase

import com.yuruneji.camera_training.common.response.LogUploadResponse
import com.yuruneji.camera_training.data.remote.AppResponse
import com.yuruneji.camera_training.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class LogUploadUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(log: MultipartBody.Part): Flow<LogUploadResponse<AppResponse>> = flow {
        try {
            emit(LogUploadResponse.Loading())
            val data = repository.log(log)
            emit(LogUploadResponse.Success(data))
        } catch (e: Exception) {
            emit(LogUploadResponse.Failure(e))
        }
    }
}
