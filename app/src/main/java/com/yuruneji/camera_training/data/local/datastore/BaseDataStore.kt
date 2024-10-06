package com.yuruneji.camera_training.data.local.datastore

import android.content.Context
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class BaseDataStore @Inject constructor(
    context: Context
) : DataStoreWrapper(context, PREF_NAME) {
    companion object {
        private const val PREF_NAME = "base_datastore"
    }
}
