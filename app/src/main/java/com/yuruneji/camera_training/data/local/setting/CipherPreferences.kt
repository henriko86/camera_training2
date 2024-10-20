package com.yuruneji.camera_training.data.local.setting

import android.content.Context

/**
 * @author toru
 * @version 1.0
 */
class CipherPreferences(context: Context) : BasePreferences(context, PREF_NAME) {
    companion object {
        /** 設定ファイル名 */
        const val PREF_NAME = "cipher_preferences"

        /** IVキー */
        private const val IV = "iv"
    }

    /**
     * IVキー
     */
    var iv: String
        get() = getEncryptString(IV)
        set(value) = setEncryptString(IV, value)

}
