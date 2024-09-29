package com.yuruneji.camera_training2.presentation.log_view.model

import com.yuruneji.camera_training2.data.local.LogEntity

/**
 * @author toru
 * @version 1.0
 */
data class TasksUiModel(
    val tasks: List<LogEntity>,
    val priorityVerbose: Boolean,
    val priorityDebug: Boolean,
    val priorityInfo: Boolean,
    val priorityWarn: Boolean,
    val priorityError: Boolean,
    val priorityAssert: Boolean
)
