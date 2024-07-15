package com.yuruneji.cameratraining2.presentation.home.state

/**
 * @author toru
 * @version 1.0
 */
data class AuthState(
    /** カード認証結果 */
    var isCardAuth: Boolean = false,
    var cardType: Int = -1,
    var cardId: Int = -1,

    /** 顔認証結果 */
    var isFaceAuth: Boolean = false,
    var userId: String = "",

    /** 認証後処理結果 */
    var isPostProcessAuth: Boolean = false

)
