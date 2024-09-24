package com.yuruneji.camera_training2.domain.usecase

import com.yuruneji.camera_training2.common.NetworkResponse
import com.yuruneji.camera_training2.data.remote.AppResponse
import com.yuruneji.camera_training2.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class LogUseCase @Inject constructor(
    private val repository: AppRepository,
) {
    operator fun invoke(log: MultipartBody.Part): Flow<NetworkResponse<AppResponse>> = flow {
        try {
            emit(NetworkResponse.Loading())
            val data = repository.log(log)
            emit(NetworkResponse.Success(data))
        } catch (e: Exception) {
            emit(NetworkResponse.Failure(e))
        }
    }
}
