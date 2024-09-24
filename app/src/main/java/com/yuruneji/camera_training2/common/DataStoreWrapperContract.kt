package com.yuruneji.camera_training2.common

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

/**
 * @author toru
 * @version 1.0
 */
interface DataStoreWrapperContract {
    /**
     * DataStore に値を書き込む
     * @return 書き込みに成功したら true, 失敗（例外がスローされた）したら false
     */
    suspend fun <T> writeValue(key: Preferences.Key<T>, value: T)

    fun <T> readValue(key: Preferences.Key<T>, defaultValue: T): Flow<T>

    /**
     * DataStore から値を削除する
     * @return 削除に成功したら true, 失敗（例外がスローされた）したら false
     */
    suspend fun <T> removeValue(key: Preferences.Key<T>): Boolean
}
