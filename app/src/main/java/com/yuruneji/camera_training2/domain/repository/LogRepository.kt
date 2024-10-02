package com.yuruneji.camera_training2.domain.repository

import com.yuruneji.camera_training2.data.local.datastore.LogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * @author toru
 * @version 1.0
 */
interface LogRepository {
    fun log(date: LocalDateTime): Flow<List<LogEntity>>
    fun log(date: LocalDateTime, priority: IntArray): Flow<List<LogEntity>>
    fun log(from: LocalDateTime, to: LocalDateTime, priority: IntArray): Flow<List<LogEntity>>
}
