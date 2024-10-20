package com.yuruneji.camera_training.common

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date


/**
 * @author toru
 * @version 1.0
 */
object DateTimeUtils {

    /**
     * 時間がの範囲内か判定
     * @param now
     * @param start
     * @param end
     * @return
     */
    fun betweenDate(now: LocalDateTime, start: LocalDateTime, end: LocalDateTime, isEqual: Boolean = true): Boolean {
        if (isEqual && (start.isEqual(now) || end.isEqual(now))) {
            return true
        }
        return start.isBefore(now) && end.isAfter(now)
    }

    /**
     * LocalDateTimeをDateに変換
     * @param dateTime LocalDateTime
     * @return java.util.Date
     */
    fun toDate(dateTime: LocalDateTime): Date {
        val zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault())
        val instant = zonedDateTime.toInstant()
        return Date.from(instant)
    }

    /**
     * java.util.DateをLocalDateTimeに変換
     * @param date java.util.Date
     * @return LocalDateTime
     */
    fun toLocalDateTime(date: Date): LocalDateTime {
        val instant = date.toInstant()
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getDate(millis: Long): Date {
        return Date(millis)
    }

    fun getLocalDateTime(millis: Long): LocalDateTime {
        return toLocalDateTime(getDate(millis))
    }
}
