package com.yuruneji.camera_training.presentation.log_view.state

import java.time.LocalDate

/**
 * @author toru
 * @version 1.0
 */
data class LogViewState(
    var date: LocalDate? = LocalDate.now(),
    // var time: LocalTime? = null,
    var period: LogPeriod = LogPeriod.DAY,
    var priorityVerbose: Boolean = true,
    var priorityDebug: Boolean = true,
    var priorityInfo: Boolean = true,
    var priorityWarn: Boolean = true,
    var priorityError: Boolean = true,
    var priorityAssert: Boolean = true
)

enum class LogPeriod {
    DAY, HALF_DAY, HOUR6, HOUR3, HOUR
}
