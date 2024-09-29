package com.yuruneji.camera_training2.domain.repository

import com.yuruneji.camera_training2.data.repository.LogViewPreferences
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * @author toru
 * @version 1.0
 */
interface LogViewPreferencesRepository {
    fun logViewPreferencesFlow(): Flow<LogViewPreferences>
    suspend fun updateDate(date: LocalDateTime)
    suspend fun updatePriority(priority: BooleanArray)
    suspend fun fetchInitialPreferences(): LogViewPreferences
}
