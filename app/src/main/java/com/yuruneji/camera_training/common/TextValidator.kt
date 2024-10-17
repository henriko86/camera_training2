package com.yuruneji.camera_training.common

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text

/**
 * @author toru
 * @version 1.0
 */
abstract class TextValidator(
    private val layout: TextInputLayout,
    private val editText: TextInputEditText,
    private val item: TextValidatorItem
) : TextWatcher {

    /**
     * バリデーション
     * @param layout
     * @param editText
     * @param text
     */
    abstract fun validate(layout: TextInputLayout, editText: TextInputEditText, text: String?)

    /** 入力タイプ */
    private var inputType: Int = InputType.TYPE_NULL

    init {
        inputType = editText.inputType
    }

    override fun afterTextChanged(s: Editable) {
        val text = editText.text.toString()

        var error = false
        if (item.isEmpty != null) {
            error = emptyValidate(text) // 未入力チェック
        }

        if (!error && item.minLength != null) {
            error = minLengthValidate(text) // 最小文字数チェック
        }

        if (!error && item.maxLength != null) {
            error = maxLengthValidate(text) // 最大文字数チェック
        }

        if (!error) {
            layout.error = null
        }

        if (!error) {
            validate(layout, editText, text)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        //
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //
    }

    /**
     * 入力チェック
     * @param text 入力文字
     */
    private fun emptyValidate(text: String): Boolean {
        item.isEmpty?.let {
            if (it) {
                if (text.isEmpty()) {
                    layout.error = item.isEmptyMsg
                    return true
                }
            }
        }
        return false
    }

    /**
     * 最小文字数チェック
     * @param text 入力文字
     */
    private fun minLengthValidate(text: String): Boolean {
        item.minLength?.let {
            if (text.length < item.minLength) {
                layout.error = item.minLengthMsg
                return true
            }
        }
        return false
    }

    /**
     * 最大文字数チェック
     * @param text 入力文字
     */
    private fun maxLengthValidate(text: String): Boolean {
        item.maxLength?.let {
            if (text.length > item.maxLength) {
                layout.error = item.maxLengthMsg
                return true
            }
        }
        return false
    }
}

// private fun textValidate(text: String) {
//     isEmpty?.let {
//         if (it) {
//             if (text.isEmpty()) {
//                 layout.error = "入力してください"
//             } else {
//                 layout.error = null
//             }
//         }
//     }
//
//     minLength?.let {
//         if (text.length < it) {
//             layout.error = "文字数が少ないです"
//         } else {
//             layout.error = null
//         }
//     }
//
//     maxLength?.let {
//         if (text.length > it) {
//             layout.error = "文字数が多すぎます"
//         } else {
//             layout.error = null
//         }
//     }
// }
// }

data class TextValidatorItem(
    val isEmpty: Boolean? = false,
    val isEmptyMsg: String = "入力してください",
    val minLength: Int? = null,
    val minLengthMsg: String? = "文字数が少ないです",
    val maxLength: Int? = null,
    val maxLengthMsg: String? = "文字数が多すぎます",
)
