package com.yuruneji.cameratraining2.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * @author toru
 * @version 1.0
 */
class DataProvider(context: Context) {

    private var sharedPreference: SharedPreferences
    private var editor: SharedPreferences.Editor
    private var encryptedSharedPref: SharedPreferences
    private var encryptedEditor: SharedPreferences.Editor


    companion object {
        private const val PREF_NAME = "SAMPLE17_PREF"
        private const val ENCRYPTED_PREF_NAME = "SAMPLE17_ENCRYPTED_PREF"

        private const val USER_NAME = "userName"
        private const val USER_PASS = "userPass"
    }

    init {
        sharedPreference = context.applicationContext
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreference.edit()

        val mainKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        encryptedSharedPref = EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREF_NAME,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        encryptedEditor = encryptedSharedPref.edit()
    }

    fun getUserName(): String? {
        return sharedPreference.getString(USER_NAME, "")
    }

    fun setUserName(userName: String) {
        editor.putString(USER_NAME, userName)
        editor.commit()
    }

    fun getUserPass(): String? {
        return encryptedSharedPref.getString(USER_PASS, "")
    }

    fun setUserPass(userPass: String) {
        encryptedEditor.putString(USER_PASS, userPass)
        encryptedEditor.commit()
    }

}
