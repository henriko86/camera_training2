package com.yuruneji.camera_training2.common

/**
 * @author toru
 * @version 1.0
 */
enum class AuthType {
    /** 顔認証 */
    FACE,

    /** カード認証 */
    CARD,

    /** QR認証 */
    QR
}

enum class AuthMethod {
    /** 単要素認証 */
    Single,

    /** 多要素認証 */
    Multi
}
