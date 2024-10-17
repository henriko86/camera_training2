package com.yuruneji.camera_training.domain.usecase

import com.yuruneji.camera_training.common.response.FaceAuthResponse
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
class CardAuthUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(request: AppRequestModel): Flow<FaceAuthResponse<AppRequestModel, AppResponseModel>> = flow {
        try {
            emit(FaceAuthResponse.Loading())
            val data = repository.cardAuth(request.toConvert()).toConvert()
            emit(FaceAuthResponse.Success(request, data))
        } catch (e: Exception) {
            emit(FaceAuthResponse.Failure(request, e))
        }
    }
}
