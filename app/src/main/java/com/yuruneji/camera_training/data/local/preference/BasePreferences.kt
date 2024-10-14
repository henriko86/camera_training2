package com.yuruneji.camera_training.data.local.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * @author toru
 * @version 1.0
 */
abstract class BasePreferences(
    context: Context,
    prefName: String = PREF_NAME,
    prefEncryptName: String = prefName
) {

    private var sharedPref: SharedPreferences = context.applicationContext.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPref.edit()

    private var encryptSharedPref: SharedPreferences
    private var encryptEditor: SharedPreferences.Editor

    companion object {
        const val PREF_NAME = "base_preferences"
    }

    init {
        val mainKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptSharedPref = EncryptedSharedPreferences.create(
            context,
            prefEncryptName,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        encryptEditor = encryptSharedPref.edit()
    }

    fun getEncryptString(key: String, defValue: String = ""): String {
        return encryptSharedPref.getString(key, defValue) ?: defValue
    }

    fun setEncryptString(key: String, value: String) {
        encryptEditor.apply {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, defValue: String = ""): String {
        return sharedPref.getString(key, defValue) ?: defValue
    }

    fun setString(key: String, value: String) {
        editor.apply {
            putString(key, value)
            apply()
        }
    }

    fun getInt(key: String, defValue: Int = 0): Int {
        return sharedPref.getInt(key, defValue)
    }

    fun setInt(key: String, value: Int) {
        editor.apply {
            putInt(key, value)
            apply()
        }
    }

    fun getLong(key: String, defValue: Long = 0): Long {
        return sharedPref.getLong(key, defValue)
    }

    fun setLong(key: String, value: Long) {
        editor.apply {
            putLong(key, value)
            apply()
        }
    }

    fun getFloat(key: String, defValue: Float = 0f): Float {
        return sharedPref.getFloat(key, defValue)
    }

    fun setFloat(key: String, value: Float) {
        editor.apply {
            putFloat(key, value)
            apply()
        }
    }

    fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, defValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        editor.apply {
            putBoolean(key, value)
            apply()
        }
    }

    fun getSetString(key: String, defValue: Set<String> = emptySet()): Set<String> {
        return sharedPref.getStringSet(key, defValue) ?: defValue
    }

    fun setSetString(key: String, value: Set<String>) {
        editor.apply {
            putStringSet(key, value)
            apply()
        }
    }
}
