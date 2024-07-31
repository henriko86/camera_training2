package com.yuruneji.cameratraining2.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * @author toru
 * @version 1.0
 */
open class DataProvider(context: Context) {

    private var sharedPref: SharedPreferences = context.applicationContext
        .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPref.edit()
    private var encSharedPref: SharedPreferences
    private var encEditor: SharedPreferences.Editor


    companion object {
        private const val PREF_NAME = "SAMPLE17_PREF"
        private const val ENCRYPTED_PREF_NAME = "SAMPLE17_ENCRYPTED_PREF"

        private const val USER_NAME = "userName"
        private const val USER_PASS = "userPass"
    }

    init {
        val mainKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        encSharedPref = EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREF_NAME,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        encEditor = encSharedPref.edit()
    }

    fun getUserName(): String? {
        return sharedPref.getString(USER_NAME, "")
    }

    fun setUserName(userName: String) {
        editor.putString(USER_NAME, userName)
        editor.commit()
    }

    fun getUserPass(): String? {
        return encSharedPref.getString(USER_PASS, "")
    }

    fun setUserPass(userPass: String) {
        encEditor.putString(USER_PASS, userPass)
        encEditor.commit()
    }

}
