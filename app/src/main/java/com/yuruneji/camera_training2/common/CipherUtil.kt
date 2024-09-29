package com.yuruneji.camera_training2.common

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.apache.commons.lang3.RandomStringUtils
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * @author toru
 * @version 1.0
 */
class CipherUtil(context: Context) {

    /** KeyStore */
    private lateinit var keyStore: KeyStore

    /** IV */
    private lateinit var iv: String

    init {
        try {
            // IVを取得
            iv = getIv(context)

            // KeyStoreのインスタンスをロード
            keyStore = KeyStore.getInstance(KEY_PROVIDER).apply {
                load(null)
            }

            // val aliases: Enumeration<String> = keyStore.aliases()
            // aliases.asSequence().forEach { alias ->
            //     Timber.d("alias=$alias")
            // }

            createAESKey()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * AESの鍵を作成する
     */
    private fun createAESKey() {
        try {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_PROVIDER)
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setRandomizedEncryptionRequired(false)
                        .build()
                )
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * 文字列をAES鍵で暗号化して返す
     *
     * @param plainText 文字列
     * @return 暗号化した文字列
     */
    fun encrypt(plainText: String): String? {
        try {
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey

            val cipher = Cipher.getInstance(ALGORITHM)
            val ivParameterSpec = IvParameterSpec(iv.toByteArray())
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

            ByteArrayOutputStream().use { outputStream ->
                CipherOutputStream(outputStream, cipher).use { cipherOutputStream ->
                    cipherOutputStream.write(plainText.toByteArray(charset("UTF-8")))
                    cipherOutputStream.close()
                    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    /**
     * 文字列をAES鍵で復号して返す
     *
     * @param encryptedText 文字列
     * @return 復号した文字列
     */
    fun decrypt(encryptedText: String): String? {
        try {
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey

            val cipher = Cipher.getInstance(ALGORITHM)
            val ivParameterSpec = IvParameterSpec(iv.toByteArray())
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)

            CipherInputStream(ByteArrayInputStream(Base64.decode(encryptedText, Base64.NO_WRAP)), cipher).use { cipherInputStream ->
                ByteArrayOutputStream().use { byteArrayOutputStream ->
                    var buffer: Int
                    while ((cipherInputStream.read().also { buffer = it }) != -1) {
                        byteArrayOutputStream.write(buffer)
                    }
                    byteArrayOutputStream.close()
                    return byteArrayOutputStream.toString("UTF-8")
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    /**
     * IVを取得
     */
    private fun getIv(context: Context): String {
        val settingValue = SettingValue(context, PREF_NAME)
        var iv = settingValue.getEncString(PREF_IV_KEY, "")
        if (iv.isEmpty()) {
            iv = RandomStringUtils.randomAlphabetic(16)
            settingValue.setEncString(PREF_IV_KEY, iv)
        }
        return iv
    }

    companion object {

        /** プロバイダー */
        private const val KEY_PROVIDER: String = "AndroidKeyStore"

        /** エイリアス */
        private const val KEY_ALIAS: String = "CipherUtil"

        /** アルゴリズム */
        private const val ALGORITHM: String = "AES/CBC/PKCS7Padding"

        /** SharedPreferences */
        private const val PREF_NAME = "CipherUtil"

        /** IVキー */
        private const val PREF_IV_KEY = "iv"
    }
}
