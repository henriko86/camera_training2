package com.yuruneji.camera_training2.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime

/**
 * @author toru
 * @version 1.0
 */
class DateTimeConverter {
    @TypeConverter
    fun toDate(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(value) }
    }

    @TypeConverter
    fun toDateString(dateTime: LocalDateTime?): String? {
        return dateTime?.toString()
    }
}
