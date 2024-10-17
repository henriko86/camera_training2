package com.yuruneji.camera_training.common

import android.os.Build
import java.time.LocalDateTime

/**
 * @author toru
 * @version 1.0
 */
object DateTimeUtils {

    fun getDateTime(): LocalDateTime {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

}
