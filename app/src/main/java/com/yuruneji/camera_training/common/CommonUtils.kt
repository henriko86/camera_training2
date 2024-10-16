package com.yuruneji.camera_training.common

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Size
import android.view.View
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date


/**
 * @author toru
 * @version 1.0
 */
object CommonUtils {


    /**
     * カメラIDを取得
     * @param context
     * @return カメラID
     */
    fun getCameraID(context: Context): CameraID {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
        val cameraID = CameraID()
        cameraManager.cameraIdList.forEach { _ ->
            val backCameraId = cameraManager.cameraIdList.first { cameraManager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK }
            val frontCameraId = cameraManager.cameraIdList.first { cameraManager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT }
            cameraID.back = backCameraId
            cameraID.front = frontCameraId
        }
        return cameraID
    }

    /**
     * 画面サイズを取得
     * @param context
     * @return 画面サイズ
     */
    fun getWindowSize(context: Context): Size {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size: Size
        if (Build.VERSION.SDK_INT <= VERSION_CODES.R) {
            val point = Point()
            windowManager.defaultDisplay.getRealSize(point)
            val width = point.x
            val height = point.y
            size = Size(width, height)
        } else {
            val wm: WindowMetrics = windowManager.currentWindowMetrics
            val width = wm.bounds.width()
            val height = wm.bounds.height()
            size = Size(width, height)
        }
        return size
    }


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

    fun toDate(dateTime: LocalDateTime): Date {
        val zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault())
        val instant = zonedDateTime.toInstant()
        return Date.from(instant)
    }

    fun toLocalDateTime(date: Date): LocalDateTime {
        val instant = date.toInstant()
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    // fun byteHexIntSum(list: List<Byte>): Int {
    //     var sum = 0
    //     for (b in list) {
    //         sum += byteHexIntValue(b)
    //     }
    //     return sum
    // }

    // fun byteHexIntValue(b: Byte): Int {
    //     val hex = String.format("%02x", b)
    //     val intValue = hex.toInt(16)
    //     return intValue
    // }

    // fun ascii2String(list: List<Byte>, def: String = ""): String {
    //     val data = ByteArray(list.size)
    //     if (list.isNotEmpty()) {
    //         for (i in list.indices) {
    //             data[i] = list[i]
    //         }
    //     }
    //     return ascii2String(data, def)
    // }

    // fun ascii2String(data: ByteArray, def: String = ""): String {
    //     try {
    //         return String(data, charset("US-ASCII"))
    //     } catch (e: UnsupportedEncodingException) {
    //         e.printStackTrace()
    //         return def
    //     }
    // }

    // fun string2Ascii(str: String): ByteArray {
    //     return str.toByteArray(Charsets.US_ASCII)
    // }

    // fun int2AsciiString(i: Int, separator: String = ""): String {
    //     return string2AsciiString(i.toString(), separator)
    // }

    // fun int2AsciiString(i: Int, size: Int, separator: String = ""): String {
    //     return string2AsciiString(String.format("%0${size}d", i), separator)
    // }

    // fun string2AsciiString(str: String, separator: String = ""): String {
    //     return str.toByteArray(Charsets.US_ASCII).joinToString(separator)
    // }

    /**
     * フルスクリーン表示
     */
    fun fullscreen(activity: Activity, state: Boolean) {
        if (state) {
            activity.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            val flags = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            activity.window?.decorView?.systemUiVisibility = flags
            (activity as? AppCompatActivity)?.supportActionBar?.hide()
        } else {
            activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            activity.window?.decorView?.systemUiVisibility = 0
            (activity as? AppCompatActivity)?.supportActionBar?.show()
        }
    }

}
