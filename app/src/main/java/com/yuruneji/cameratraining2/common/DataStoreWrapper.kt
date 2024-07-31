package com.yuruneji.cameratraining2.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer.defaultValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author toru
 * @version 1.0
 */
class DataStoreWrapper(private val context: Context, fileName: String) : DataStoreWrapperContract {

    // At the top level of your kotlin file:
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings3")


    // private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    //     name = fileName,
    //     // produceMigrations = { context ->
    //     //     listOf(
    //     //         SharedPreferencesMigration(
    //     //             context = context,
    //     //             sharedPreferencesName = fileName,
    //     //             keysToMigrate = setOf(
    //     //                 //
    //     //             )
    //     //         )
    //     //     )
    //     // }
    // )

    override suspend fun <T> writeValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override fun <T> readValue(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    override suspend fun <T> removeValue(key: Preferences.Key<T>): Boolean =
        runCatching {
            context.dataStore.edit { preferences ->
                preferences.remove(key)
            }
        }.isSuccess


    val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
    val exampleCounterFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[EXAMPLE_COUNTER] ?: 0
        }
    suspend fun incrementCounter() {
        context.dataStore.edit { settings ->
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
    }

    // fun getUserName(key: Preferences.Key<String>, defaultValue: String): Flow<String> {
    //     return readValue(key, defaultValue)
    // }

    // fun setUserName() {
    //
    // }

    // companion object {
    //
    // }

}
