package com.yuruneji.camera_training2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * @author toru
 * @version 1.0
 */
@Entity(tableName = "log")
data class LogEntity(
    @PrimaryKey()
    val date: LocalDateTime,
    val priority: Int,
    val tag: String?,
    val message: String,
    val throwable: String?
)
