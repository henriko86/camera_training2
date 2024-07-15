package com.yuruneji.cameratraining2.presentation.dashboard.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

/**
 * @author toru
 * @version 1.0
 */
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    /**
     * Fragment に選択結果を渡すためのリスナー
     */
    interface OnSelectedDateListener {
        fun selectedDate(year: Int, month: Int, dayOfMonth: Int)
    }

    /** Fragment に選択結果を渡すためのリスナー */
    private lateinit var listener: OnSelectedDateListener

    /**
     * フラグメントがそのコンテキストに最初にアタッチされるときに呼び出されます。
     * この後、onCreate(android.os.Bundle) が呼び出されます。
     * このメソッドをオーバーライドする場合は、スーパークラス実装を呼び出す必要があります。
     * @param[context] Context
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnSelectedDateListener) {
            listener = parentFragment as OnSelectedDateListener
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // return super.onCreateDialog(savedInstanceState)
        val localDate = LocalDate.now()
        return DatePickerDialog(
            this.context as Context,
            this,
            localDate.year,
            localDate.monthValue - 1,
            localDate.dayOfMonth
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener.selectedDate(year, month, dayOfMonth)
    }
}
