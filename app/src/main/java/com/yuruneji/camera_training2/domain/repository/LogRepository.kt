package com.yuruneji.camera_training2.domain.repository

import com.yuruneji.camera_training2.data.local.LogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * @author toru
 * @version 1.0
 */
interface LogRepository {
    // fun log(): Flow<List<LogEntity>>
    fun log(date: LocalDateTime): Flow<List<LogEntity>>
    // suspend fun fetchInitialPreferences(): UserPreferences
    // suspend fun updateDebugShowDebug(show: Boolean)
}
