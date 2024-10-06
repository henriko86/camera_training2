package com.yuruneji.camera_training.common

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * @author toru
 * @version 1.0
 */
abstract class TextValidator(
    private val layout: TextInputLayout,
    private val editText: TextInputEditText
) : TextWatcher {
    abstract fun validate(layout: TextInputLayout, editText: TextInputEditText, text: String?)

    override fun afterTextChanged(s: Editable) {
        val text = editText.text.toString()
        validate(layout, editText, text)
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
        /* Don't care */
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        /* Don't care */
    }
}
