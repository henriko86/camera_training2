package com.yuruneji.camera_training

import android.util.Size
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yuruneji.camera_training.common.CommonUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author toru
 * @version 1.0
 */
@RunWith(AndroidJUnit4::class)
class CommonUtilsTest {

    // @Test
    // fun useAppContext() {
    //     // Context of the app under test.
    //     val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    //     assertEquals("com.yuruneji.camera_training2", appContext.packageName)
    // }

    // @Test
    // fun cameraIdFront() {
    //     val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    //     val cameraID = CommonUtils.getCameraID(appContext)
    //
    //     assertEquals(1, cameraID.front.toInt())
    // }

    // @Test
    // fun cameraIdBack() {
    //     val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    //     val cameraID = CommonUtils.getCameraID(appContext)
    //
    //     assertEquals(0, cameraID.back.toInt())
    // }

    // @Test
    // fun windowSize() {
    //     val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    //     val size = CommonUtils.getWindowSize(appContext)
    //
    //     assertEquals(Size(1080, 2400), size)
    // }

    private val startDateTime = LocalTime.of(8, 30, 0).atDate(LocalDate.now())
    private val endDateTime = LocalTime.of(17, 30, 0).atDate(LocalDate.now())

    @Test
    fun betweenDateTest1() {
        val now = LocalTime.of(8, 29, 0).atDate(LocalDate.now())
        val result = CommonUtils.betweenDate(now, startDateTime, endDateTime)
        assertEquals(false, result)
    }

    @Test
    fun betweenDateTest2() {
        val now = LocalTime.of(8, 30, 0).atDate(LocalDate.now())
        val result = CommonUtils.betweenDate(now, startDateTime, endDateTime)
        assertEquals(true, result)
    }

    @Test
    fun betweenDateTest3() {
        val now = LocalTime.of(8, 31, 0).atDate(LocalDate.now())
        val result = CommonUtils.betweenDate(now, startDateTime, endDateTime)
        assertEquals(true, result)
    }

    @Test
    fun betweenDateTest4() {
        val now = LocalTime.of(17, 29, 0).atDate(LocalDate.now())
        val result = CommonUtils.betweenDate(now, startDateTime, endDateTime)
        assertEquals(true, result)
    }

    @Test
    fun betweenDateTest5() {
        val now = LocalTime.of(17, 30, 0).atDate(LocalDate.now())
        val result = CommonUtils.betweenDate(now, startDateTime, endDateTime)
        assertEquals(true, result)
    }

    @Test
    fun betweenDateTest6() {
        val now = LocalTime.of(17, 31, 0).atDate(LocalDate.now())
        val result = CommonUtils.betweenDate(now, startDateTime, endDateTime)
        assertEquals(false, result)
    }

    // @Test
    // fun hoge2() {
    //     assertEquals(4, 2 + 2)
    // }

}
