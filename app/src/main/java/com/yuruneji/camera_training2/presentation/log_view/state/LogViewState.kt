package com.yuruneji.camera_training2.presentation.log_view.state

import java.time.LocalDate

/**
 * @author toru
 * @version 1.0
 */
data class LogViewState(
    val date: LocalDate = LocalDate.now(),
    val priorityVerbose: Boolean = true,
    val priorityDebug: Boolean = true,
    val priorityInfo: Boolean = true,
    val priorityWarn: Boolean = true,
    val priorityError: Boolean = true,
    val priorityAssert: Boolean = true
)
