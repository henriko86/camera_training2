package com.yuruneji.cameratraining2.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author toru
 * @version 1.0
 */
class DataStoreWrapper(private val context: Context, fileName: String) : DataStoreWrapperContract {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = fileName,
        // produceMigrations = { context ->
        //     listOf(
        //         SharedPreferencesMigration(
        //             context = context,
        //             sharedPreferencesName = fileName,
        //             keysToMigrate = setOf(
        //                 //
        //             )
        //         )
        //     )
        // }
    )

    override suspend fun <T> writeValue(key: Preferences.Key<T>, value: T): Boolean =
        runCatching {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }.isSuccess

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

}
