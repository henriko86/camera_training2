package com.yuruneji.cameratraining2.common

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.apache.commons.lang3.SystemUtils

/**
 * @author toru
 * @version 1.0
 */
class UserDataProvider(context: Context) : DataProvider(context, PREF_NAME) {

    companion object {
        private const val PREF_NAME = "user.setting"

        private const val USER_NAME = "userName"
        private const val USER_PASS = "userPass"
    }

    init {
        //
    }

    fun getUserName(): String {
        return getString(USER_NAME, "")
    }

    fun setUserName(value: String) {
        setString(USER_NAME, value)
    }

    fun getUserPass(): String? {
        return getEncString(USER_PASS, "")
    }

    fun setUserPass(value: String) {
        setEncString(USER_PASS, value)
    }
}
