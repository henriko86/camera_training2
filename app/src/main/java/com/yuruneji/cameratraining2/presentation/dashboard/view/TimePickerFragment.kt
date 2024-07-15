package com.yuruneji.cameratraining2.presentation.dashboard.view

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalTime

/**
 * @author toru
 * @version 1.0
 */
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    /**
     * Fragment に選択結果を渡すためのリスナー
     */
    interface OnSelectedTimeListener {
        fun selectedTime(hour: Int, minute: Int)
    }

    /** Fragment に選択結果を渡すためのリスナー */
    private lateinit var listener: OnSelectedTimeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnSelectedTimeListener) {
            listener = parentFragment as OnSelectedTimeListener
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // return super.onCreateDialog(savedInstanceState)
        // Use the current time as the default values for the picker.
        // val c = Calendar.getInstance()
        // val hour = c.get(Calendar.HOUR_OF_DAY)
        // val minute = c.get(Calendar.MINUTE)
        //
        // // Create a new instance of TimePickerDialog and return it.
        // return TimePickerDialog(
        //     activity,
        //     this,
        //     hour,
        //     minute,
        //     DateFormat.is24HourFormat(activity)
        // )
        val localTime = LocalTime.now()
        return TimePickerDialog(
            this.context as Context,
            this,
            localTime.hour,
            localTime.minute,
            true
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener.selectedTime(hourOfDay, minute)
    }
}
