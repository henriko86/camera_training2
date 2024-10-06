package com.yuruneji.camera_training.domain.usecase

import com.yuruneji.camera_training.common.AuthResponse
import com.yuruneji.camera_training.data.remote.toConvert
import com.yuruneji.camera_training.domain.model.AppRequestModel
import com.yuruneji.camera_training.domain.model.AppResponseModel
import com.yuruneji.camera_training.domain.model.toConvert
import com.yuruneji.camera_training.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class FaceAuthUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(request: AppRequestModel): Flow<AuthResponse<AppRequestModel, AppResponseModel>> = flow {
        try {
            emit(AuthResponse.Loading())
            val data = repository.faceAuth(request.toConvert()).toConvert()
            emit(AuthResponse.Success(request, data))
        } catch (e: Exception) {
            emit(AuthResponse.Failure(request, e))
        }
    }
}
