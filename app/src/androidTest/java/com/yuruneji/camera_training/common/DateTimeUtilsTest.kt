package com.yuruneji.camera_training.common

import org.junit.Assert
import java.time.LocalDate
import java.time.LocalTime

/**
 * @author toru
 * @version 1.0
 */
class DateTimeUtilsTest {

    // @Test
    // fun betweenDate() {
    // }

    private val startDateTime = LocalTime.of(8, 30, 0).atDate(LocalDate.now())
    private val endDateTime = LocalTime.of(17, 30, 0).atDate(LocalDate.now())

    @org.junit.Test
    fun betweenDateTest1() {
        val now = LocalTime.of(8, 29, 0).atDate(LocalDate.now())
        val result = DateTimeUtils.betweenDate(now, startDateTime, endDateTime)
        Assert.assertEquals(false, result)
    }

    @org.junit.Test
    fun betweenDateTest2() {
        val now = LocalTime.of(8, 30, 0).atDate(LocalDate.now())
        val result = DateTimeUtils.betweenDate(now, startDateTime, endDateTime)
        Assert.assertEquals(true, result)
    }

    @org.junit.Test
    fun betweenDateTest3() {
        val now = LocalTime.of(8, 31, 0).atDate(LocalDate.now())
        val result = DateTimeUtils.betweenDate(now, startDateTime, endDateTime)
        Assert.assertEquals(true, result)
    }

    @org.junit.Test
    fun betweenDateTest4() {
        val now = LocalTime.of(17, 29, 0).atDate(LocalDate.now())
        val result = DateTimeUtils.betweenDate(now, startDateTime, endDateTime)
        Assert.assertEquals(true, result)
    }

    @org.junit.Test
    fun betweenDateTest5() {
        val now = LocalTime.of(17, 30, 0).atDate(LocalDate.now())
        val result = DateTimeUtils.betweenDate(now, startDateTime, endDateTime)
        Assert.assertEquals(true, result)
    }

    @org.junit.Test
    fun betweenDateTest6() {
        val now = LocalTime.of(17, 31, 0).atDate(LocalDate.now())
        val result = DateTimeUtils.betweenDate(now, startDateTime, endDateTime)
        Assert.assertEquals(false, result)
    }

    // @Test
    // fun toDate() {
    // }
    //
    // @Test
    // fun toLocalDateTime() {
    // }
    //
    // @Test
    // fun currentTimeMillis() {
    // }
    //
    // @Test
    // fun getDate() {
    // }
    //
    // @Test
    // fun getLocalDateTime() {
    // }

    // @Test
    // fun getJapaneseDate() {
    //     val result = DateTimeUtils.getJapaneseDate()
    //
    //     Assert.assertEquals("", result)
    // }
}
