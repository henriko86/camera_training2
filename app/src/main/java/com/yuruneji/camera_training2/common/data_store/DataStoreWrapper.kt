package com.yuruneji.camera_training2.common.data_store

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
abstract class DataStoreWrapper(private val context: Context, fileName: String) : DataStoreWrapperContract {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = fileName)

    fun getDataStore(): DataStore<Preferences> {
        return context.dataStore
    }

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


    // fun getUserName(): Flow<String> {
    //     return readValue(USER_NAME, "")
    // }
    //
    // suspend fun setUserName(name: String) {
    //     writeValue(USER_NAME, name)
    // }
    //
    // suspend fun clearUserName() {
    //     removeValue(USER_NAME)
    // }


    companion object {
        // private val USER_NAME = stringPreferencesKey(DataStoreKeys.user_name.name)
        // private val USER_PASS = stringPreferencesKey(DataStoreKeys.user_pass.name)
    }

}
