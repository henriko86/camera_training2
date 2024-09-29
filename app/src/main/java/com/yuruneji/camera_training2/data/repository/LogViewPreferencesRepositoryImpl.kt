package com.yuruneji.camera_training2.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.yuruneji.camera_training2.common.data_store.LogViewDataStore
import com.yuruneji.camera_training2.domain.repository.LogViewPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class LogViewPreferencesRepositoryImpl @Inject constructor(
    private val logViewDataStore: LogViewDataStore
) : LogViewPreferencesRepository {

    // private object PreferencesKeys {
    //     val SORT_ORDER = stringPreferencesKey("sort_order")
    //     val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    // }

    override fun logViewPreferencesFlow(): Flow<LogViewPreferences> = logViewDataStore.getDataStore().data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Timber.e("Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapLogViewPreferences(preferences)
        }

    override suspend fun updateDate(date: LocalDateTime) {
        logViewDataStore.setDate(date)
    }

    override suspend fun updatePriority(priority: BooleanArray) {
        logViewDataStore.setPriorityVerbose(priority[0])
        logViewDataStore.setPriorityDebug(priority[1])
        logViewDataStore.setPriorityInfo(priority[2])
        logViewDataStore.setPriorityWarn(priority[3])
        logViewDataStore.setPriorityError(priority[4])
        logViewDataStore.setPriorityAssert(priority[5])
    }

    override suspend fun fetchInitialPreferences(): LogViewPreferences {
        return mapLogViewPreferences(logViewDataStore.getDataStore().data.first().toPreferences())
    }

    private fun mapLogViewPreferences(preferences: Preferences): LogViewPreferences {
        // val date = preferences[LogViewDataStore.DATE] ?: LocalDate.now().format(LogViewDataStore.FORMAT)
        val priorityVerbose = preferences[LogViewDataStore.PRIORITY_VERBOSE] ?: false
        val priorityDebug = preferences[LogViewDataStore.PRIORITY_DEBUG] ?: false
        val priorityInfo = preferences[LogViewDataStore.PRIORITY_INFO] ?: false
        val priorityWarn = preferences[LogViewDataStore.PRIORITY_WARN] ?: false
        val priorityError = preferences[LogViewDataStore.PRIORITY_ERROR] ?: false
        val priorityAssert = preferences[LogViewDataStore.PRIORITY_ASSERT] ?: false

        return LogViewPreferences(
            // date,
            priorityVerbose,
            priorityDebug,
            priorityInfo,
            priorityWarn,
            priorityError,
            priorityAssert
        )
    }
}

data class LogViewPreferences(
    // val date: String,
    val priorityVerbose: Boolean,
    val priorityDebug: Boolean,
    val priorityInfo: Boolean,
    val priorityWarn: Boolean,
    val priorityError: Boolean,
    val priorityAssert: Boolean
)
