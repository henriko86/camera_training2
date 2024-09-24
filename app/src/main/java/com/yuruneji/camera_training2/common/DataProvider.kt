package com.yuruneji.camera_training2.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * @author toru
 * @version 1.0
 */
abstract class DataProvider(
    context: Context,
    prefName: String
) {

     var sharedPref: SharedPreferences = context.applicationContext
        .getSharedPreferences(prefName, Context.MODE_PRIVATE)
     var editor: SharedPreferences.Editor = sharedPref.edit()
     var encSharedPref: SharedPreferences
     var encEditor: SharedPreferences.Editor

    companion object {
        // private const val PREF_NAME = "SAMPLE17_PREF"
        // private const val ENCRYPTED_PREF_NAME = "SAMPLE17_ENCRYPTED_PREF"
    }

    init {
        val mainKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        encSharedPref = EncryptedSharedPreferences.create(
            context,
            prefName,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        encEditor = encSharedPref.edit()
    }

     fun getEncString(key: String, defValue: String): String {
        return encSharedPref.getString(key, defValue) ?: defValue
    }

     fun setEncString(key: String, value: String) {
        encEditor.apply {
            putString(key, value)
            commit()
        }
    }

     fun getString(key: String, defValue: String): String {
        return sharedPref.getString(key, defValue) ?: defValue
    }

     fun setString(key: String, value: String) {
        editor.apply {
            putString(key, value)
            commit()
        }
    }

     fun getInt(key: String, defValue: Int): Int {
        return sharedPref.getInt(key, defValue)
    }

     fun setInt(key: String, value: Int) {
        editor.apply {
            putInt(key, value)
            commit()
        }
    }

     fun getLong(key: String, defValue: Long): Long {
        return sharedPref.getLong(key, defValue)
    }

     fun setLong(key: String, value: Long) {
        editor.apply {
            putLong(key, value)
            commit()
        }
    }

     fun getFloat(key: String, defValue: Float): Float {
        return sharedPref.getFloat(key, defValue)
    }

     fun setFloat(key: String, value: Float) {
        editor.apply {
            putFloat(key, value)
            commit()
        }
    }

     fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defValue)
    }

     fun setBoolean(key: String, value: Boolean) {
        editor.apply {
            putBoolean(key, value)
            commit()
        }
    }

     fun getStringSet(key: String, defValue: Set<String>): Set<String> {
        return sharedPref.getStringSet(key, defValue) ?: defValue
    }

     fun setStringSet(key: String, value: Set<String>) {
        editor.apply {
            putStringSet(key, value)
            commit()
        }
    }
}
