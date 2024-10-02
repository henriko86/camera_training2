package com.yuruneji.camera_training2.domain.usecase

import com.yuruneji.camera_training2.common.CardFaceAuthResponse
import com.yuruneji.camera_training2.data.remote.AppRequest
import com.yuruneji.camera_training2.data.remote.toConvert
import com.yuruneji.camera_training2.domain.model.FaceAuthInfo
import com.yuruneji.camera_training2.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class CardFaceAuthUseCase @Inject constructor(
    private val repository: AppRepository,
) {
    operator fun invoke(request: AppRequest): Flow<CardFaceAuthResponse<FaceAuthInfo>> = flow {
        try {
            emit(CardFaceAuthResponse.Loading())
            val data = repository.faceAuth(request).toConvert()
            emit(CardFaceAuthResponse.Success(data))
        } catch (e: Exception) {
            emit(CardFaceAuthResponse.Failure(e))
        }
    }
}
