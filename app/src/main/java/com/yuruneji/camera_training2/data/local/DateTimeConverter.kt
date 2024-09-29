package com.yuruneji.camera_training2.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author toru
 * @version 1.0
 */
class DateTimeConverter {

    companion object {
        private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS"
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
    }

    @TypeConverter
    fun toDate(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(value, DATE_TIME_FORMATTER) }
    }

    @TypeConverter
    fun toDateString(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DATE_TIME_FORMATTER)
    }
}
