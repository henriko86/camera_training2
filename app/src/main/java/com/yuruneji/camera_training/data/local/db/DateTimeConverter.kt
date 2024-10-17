package com.yuruneji.camera_training.data.local.db

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author toru
 * @version 1.0
 */
class DateTimeConverter {

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    }

    @TypeConverter
    fun toDate(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(value, formatter) }
    }

    @TypeConverter
    fun toDateString(dateTime: LocalDateTime?): String? {
        return dateTime?.format(formatter)
    }
}
