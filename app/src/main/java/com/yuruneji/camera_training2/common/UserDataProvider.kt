package com.yuruneji.camera_training2.common

import android.content.Context

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
