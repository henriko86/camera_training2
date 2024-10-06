package com.yuruneji.camera_training.common

import android.hardware.camera2.CameraCharacteristics

/**
 * 定数
 * @author toru
 * @version 1.0
 */
object Constants {

    // /** URL */
    // const val BASE_URL = "http://192.168.11.2:8080"

    // /** カード認証　OR　顔認証 */
    // const val AUTH_TYPE_CARD_OR_FACE = 0

    // /** 顔認証 */
    // const val AUTH_TYPE_FACE = 1
    // /** カード認証 */
    // const val AUTH_TYPE_CARD = 2
    // /** カード認証　AND　顔認証 */
    // const val AUTH_TYPE_CARD_AND_FACE = 3
    // /** 前カメラ */
    // const val LENS_FACING_FRONT = CameraSelector.LENS_FACING_FRONT
    // /** 後ろカメラ */
    // const val LENS_FACING_BACK = CameraSelector.LENS_FACING_BACK

}

enum class LensFacing(val no: Int, val value: String) {
    FRONT(CameraCharacteristics.LENS_FACING_FRONT, "前カメラ"),
    BACK(CameraCharacteristics.LENS_FACING_BACK, "後カメラ");

    companion object {
        private val defItem = FRONT

        fun valueList() = enumValues<LensFacing>().map { it.value }

        fun toNo(value: String, defNo: Int = defItem.no): Int {
            for (item in enumValues<LensFacing>()) {
                if (item.value == value) {
                    return item.no
                }
            }
            return defNo
        }

        fun toValue(no: Int, defValue: String = defItem.value): String {
            for (item in enumValues<LensFacing>()) {
                if (item.no == no) {
                    return item.value
                }
            }
            return defValue
        }
    }
}

enum class ApiType(val no: Int, val value: String) {
    DEVELOP(0, "開発環境"),
    STAGING(1, "ステージング環境"),
    PRODUCTION(2, "本番環境");

    companion object {
        private val defItem = DEVELOP

        fun valueList() = enumValues<ApiType>().map { it.value }

        fun toNo(value: String, defNo: Int = defItem.no): Int {
            for (item in enumValues<ApiType>()) {
                if (item.value == value) {
                    return item.no
                }
            }
            return defNo
        }

        fun toValue(no: Int, defValue: String = defItem.value): String {
            for (item in enumValues<ApiType>()) {
                if (item.no == no) {
                    return item.value
                }
            }
            return defValue
        }
    }
}

/**
 * 認証方法
 */
enum class AuthMethod(val no: Int, val value: String) {
    SINGLE(0, "単要素認証"),
    MULTI(1, "多要素認証");

    companion object {
        private val defItem = SINGLE

        fun valueList() = enumValues<AuthMethod>().map { it.value }

        fun toNo(value: String, defNo: Int = defItem.no): Int {
            for (item in enumValues<AuthMethod>()) {
                if (item.value == value) {
                    return item.no
                }
            }
            return defNo
        }

        fun toValue(no: Int, defValue: String = defItem.value): String {
            for (item in enumValues<AuthMethod>()) {
                if (item.no == no) {
                    return item.value
                }
            }
            return defValue
        }
    }
}

/**
 * 認証タイプ
 */
enum class AuthType(val no: Int, val value: String) {
    FACE(0, "顔認証"),
    CARD(1, "カード認証"),
    QR(2, "QRコード認証");

    companion object {
        private val defItem = FACE

        fun valueList() = enumValues<AuthType>().map { it.value }

        fun toNo(value: String, defNo: Int = defItem.no): Int {
            for (item in enumValues<AuthType>()) {
                if (item.value == value) {
                    return item.no
                }
            }
            return defNo
        }

        fun toValue(no: Int, defValue: String = defItem.value): String {
            for (item in enumValues<AuthType>()) {
                if (item.no == no) {
                    return item.value
                }
            }
            return defValue
        }
    }
}

/**
 * 多要素認証タイプ
 */
enum class MultiAuthType(val no: Int, val value: String) {
    CARD_FACE(0, "カード＆顔認証"),
    QR_FACE(1, "QRコード＆顔認証");

    companion object {
        private val defItem = CARD_FACE

        fun valueList() = enumValues<MultiAuthType>().map { it.value }

        fun toNo(value: String, defNo: Int = defItem.no): Int {
            for (item in enumValues<MultiAuthType>()) {
                if (item.value == value) {
                    return item.no
                }
            }
            return defNo
        }

        fun toValue(no: Int, defValue: String = defItem.value): String {
            for (item in enumValues<MultiAuthType>()) {
                if (item.no == no) {
                    return item.value
                }
            }
            return defValue
        }
    }
}

/**
 * 顔検出サイズ
 */
enum class MinFaceSize(val no: Int, val value: String, val size: Float) {
    FaceSize1(0, "0.5", 0.5f),
    FaceSize2(1, "0.4.5", 0.45f),
    FaceSize3(2, "0.4", 0.4f),
    FaceSize4(3, "0.35", 0.35f),
    FaceSize5(4, "0.3", 0.3f),
    FaceSize6(5, "0.25", 0.25f),
    FaceSize7(6, "0.2", 0.2f),
    FaceSize8(7, "0.15", 0.15f),
    FaceSize9(8, "0.1", 0.1f);

    companion object {
        private val defItem = FaceSize8

        fun valueList() = enumValues<MinFaceSize>().map { it.value }

        fun toValue(size: Float, defValue: String = defItem.value): String {
            for (item in enumValues<MinFaceSize>()) {
                if (item.size == size) {
                    return item.value
                }
            }
            return defValue
        }

        fun toSize(value: String, defSize: Float = defItem.size): Float {
            for (item in enumValues<MinFaceSize>()) {
                if (item.value == value) {
                    return item.size
                }
            }
            return defSize
        }
    }
}

/**
 * 認証状態
 */
enum class AuthStateEnum {
    LOADING,
    SUCCESS,
    FAIL
}
