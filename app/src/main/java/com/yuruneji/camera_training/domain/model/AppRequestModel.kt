package com.yuruneji.camera_training.domain.model

import com.yuruneji.camera_training.data.remote.AppRequest

/**
 * @author toru
 * @version 1.0
 */
data class AppRequestModel(
    val img: String = "",
    val card: String = ""
)

fun AppRequestModel.toConvert(): AppRequest {
    return AppRequest(
        img = img,
        card = card
    )
}
