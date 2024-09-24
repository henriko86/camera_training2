package com.yuruneji.camera_training2.presentation.log_view.view

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
