package com.yuruneji.camera_training2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yuruneji.camera_training2.presentation.log_view.view.LogViewItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author toru
 * @version 1.0
 */
@Entity(tableName = "log")
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val date: LocalDateTime,
    val priority: Int,
    val tag: String?,
    val message: String,
    val throwable: String?
)

fun LogEntity.convert(): LogViewItem {
    val dateStr = date.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
    // val priorityStr = when (priority) {
    //     Log.VERBOSE -> "VERBOSE"
    //     Log.DEBUG -> "DEBUG"
    //     Log.INFO -> "INFO"
    //     Log.WARN -> "WARN"
    //     Log.ERROR -> "ERROR"
    //     Log.ASSERT -> "ASSERT"
    //     else -> "UNKNOWN"
    // }
    return LogViewItem(uid, dateStr, priority, tag, message)
}
