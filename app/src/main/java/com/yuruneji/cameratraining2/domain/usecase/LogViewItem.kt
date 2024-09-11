package com.yuruneji.cameratraining2.domain.usecase

/**
 * @author toru
 * @version 1.0
 */
data class LogViewItem(
    val date: String,
    val priority: String,
    val tag: String,
    val message: String,
    val throwable: String? = null
)
