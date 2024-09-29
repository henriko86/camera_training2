package com.yuruneji.camera_training2.common.data_store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class LogViewDataStore @Inject constructor(
    context: Context
) : DataStoreWrapper(context, "log_view") {

    // fun getSortOrder(): Flow<Int> {
    //     return readValue(SORT_ORDER, 0)
    // }
    // suspend fun setSortOrder(value: Int) {
    //     writeValue(SORT_ORDER, value)
    // }

    fun getDate(): Flow<String> {
        return readValue(DATE, LocalDateTime.now().format(FORMAT))
    }

    suspend fun setDate(date: LocalDateTime) {
        writeValue(DATE, date.format(FORMAT))
    }

    fun getPriorityVerbose(): Flow<Boolean> {
        return readValue(PRIORITY_VERBOSE, false)
    }

    suspend fun setPriorityVerbose(show: Boolean) {
        writeValue(PRIORITY_VERBOSE, show)
    }

    fun getPriorityDebug(): Flow<Boolean> {
        return readValue(PRIORITY_DEBUG, false)
    }

    suspend fun setPriorityDebug(show: Boolean) {
        writeValue(PRIORITY_DEBUG, show)
    }

    fun getPriorityInfo(): Flow<Boolean> {
        return readValue(PRIORITY_INFO, false)
    }

    suspend fun setPriorityInfo(show: Boolean) {
        writeValue(PRIORITY_INFO, show)
    }

    fun getPriorityWarn(): Flow<Boolean> {
        return readValue(PRIORITY_WARN, false)
    }

    suspend fun setPriorityWarn(show: Boolean) {
        writeValue(PRIORITY_WARN, show)
    }

    fun getPriorityError(): Flow<Boolean> {
        return readValue(PRIORITY_ERROR, false)
    }

    suspend fun setPriorityError(show: Boolean) {
        writeValue(PRIORITY_ERROR, show)
    }

    fun getPriorityAssert(): Flow<Boolean> {
        return readValue(PRIORITY_ASSERT, false)
    }

    suspend fun setPriorityAssert(show: Boolean) {
        writeValue(PRIORITY_ASSERT, show)
    }

    companion object {
        val FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val DATE = stringPreferencesKey("date")
        val PRIORITY_VERBOSE = booleanPreferencesKey("priority_verbose")
        val PRIORITY_DEBUG = booleanPreferencesKey("priority_debug")
        val PRIORITY_INFO = booleanPreferencesKey("priority_info")
        val PRIORITY_WARN = booleanPreferencesKey("priority_warn")
        val PRIORITY_ERROR = booleanPreferencesKey("priority_error")
        val PRIORITY_ASSERT = booleanPreferencesKey("priority_assert")
    }
}
