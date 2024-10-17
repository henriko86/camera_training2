package com.yuruneji.camera_training.domain.usecase

import com.yuruneji.camera_training.common.response.DeviceResponse
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
    operator fun invoke(log: MultipartBody.Part): Flow<DeviceResponse<AppResponse>> = flow {
        try {
            emit(DeviceResponse.Loading())
            val data = repository.log(log)
            emit(DeviceResponse.Success(data))
        } catch (e: Exception) {
            emit(DeviceResponse.Failure(e))
        }
    }
}
