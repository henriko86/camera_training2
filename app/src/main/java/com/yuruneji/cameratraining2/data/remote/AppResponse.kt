package com.yuruneji.cameratraining2.data.remote

import com.yuruneji.cameratraining2.domain.model.FaceAuthInfo

/**
 * @author toru
 * @version 1.0
 */
data class AppResponse(
    val result: Int,
    val img: String,
    val rect: String,
    val error: String?
)

fun AppResponse.toConvert(): FaceAuthInfo {
    return FaceAuthInfo(
        result = result,
        name = img,
        rect = rect
    )
}
