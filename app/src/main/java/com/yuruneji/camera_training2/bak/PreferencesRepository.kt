package com.yuruneji.camera_training2.bak

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class PreferencesRepository @Inject constructor(
    private val context: Context
) {

    companion object {
        const val PREF_NAME = "PreferencesRepository"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREF_NAME
    )

    suspend fun <T> writeValue(key: Preferences.Key<T>, value: T) {
        // val preferenceKey = booleanPreferencesKey(key.name)
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun <T> readValue(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        // val preferenceKey = booleanPreferencesKey(key.name)
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun <T> removeValue(key: Preferences.Key<T>): Boolean =
        runCatching {
            context.dataStore.edit { preferences ->
                preferences.remove(key)
            }
        }.isSuccess
}
