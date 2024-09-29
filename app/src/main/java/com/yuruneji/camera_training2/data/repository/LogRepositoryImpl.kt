package com.yuruneji.camera_training2.data.repository

import com.yuruneji.camera_training2.data.local.LogDao
import com.yuruneji.camera_training2.data.local.LogEntity
import com.yuruneji.camera_training2.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class LogRepositoryImpl @Inject constructor(
    // private val dataStore: DataStore<Preferences>,
    private val logDao: LogDao
) : LogRepository {

    // private object PreferencesKeys {
    //     val DEBUG_SHOW = booleanPreferencesKey("debug_show")
    // }

    // val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
    //     .catch { exception ->
    //         // dataStore.data throws an IOException when an error is encountered when reading data
    //         if (exception is IOException) {
    //             // Log.e(TAG, "Error reading preferences.", exception)
    //             emit(emptyPreferences())
    //         } else {
    //             throw exception
    //         }
    //     }.map { preferences ->
    //         mapUserPreferences(preferences)
    //     }

    // override fun log(): Flow<List<LogEntity>> {
    //     return logDao.getAll()
    // }

    override fun log(date: LocalDateTime): Flow<List<LogEntity>> {
        return logDao.get(date)
    }

    // override suspend fun updateDebugShowDebug(show: Boolean) {
    //     // dataStore.edit { preferences ->
    //     //     preferences[PreferencesKeys.DEBUG_SHOW] = show
    //     // }
    // }

    // override  suspend fun fetchInitialPreferences() = mapUserPreferences(dataStore.data.first().toPreferences())

    // private fun mapUserPreferences(preferences: Preferences): UserPreferences {
    //     // Get the sort order from preferences and convert it to a [SortOrder] object
    //     // val sortOrder =
    //     //     SortOrder.valueOf(
    //     //         preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.NONE.name
    //     //     )
    //     //
    //     // // Get our show completed value, defaulting to false if not set:
    //     val showCompleted = preferences[PreferencesKeys.DEBUG_SHOW] ?: false
    //     return UserPreferences(showCompleted)
    // }
}
