package com.yuruneji.camera_training.common

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.apache.commons.codec.binary.Hex
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.experimental.inv

/**
 * @author toru
 * @version 1.0
 */
object CommonUtil {
    fun drawableToBitmap(drawable: Drawable?): Bitmap {
        if (drawable == null) return Bitmap.createBitmap(1, 1, Config.ARGB_8888)

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    // fun getProperties(context: Context, id: Int): List<Prop> {
    //     val list = mutableListOf<Prop>()
    //
    //     val items = context.resources.getStringArray(id)
    //     items.forEach { s ->
    //         s.split(",").also {
    //             if (it.size == 2) {
    //                 list.add(Prop(it[0].toInt(), it[1]))
    //             }
    //         }
    //     }
    //
    //     return list
    // }


    fun flipBitmap(source: Bitmap, rotation: Int): Bitmap {
        val imageWidth: Int = source.width
        val imageHeight: Int = source.height

        val matrix = Matrix()
        matrix.setRotate(rotation.toFloat())

        return Bitmap.createBitmap(
            source, 0, 0, imageWidth, imageHeight, matrix, true
        )
    }

    fun bitmapTrim(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height

            // トリミングする幅、高さ、座標の設定
            val startX = (width - maxWidth) / 2
            val startY = (height - maxHeight) / 2
            Bitmap.createBitmap(image, startX, startY, maxWidth, maxHeight, null, true)
        } else {
            image
        }
    }

    fun faceClipping(bmp: Bitmap, faceRect: Rect): Bitmap? {
        val matrix = Matrix().also {
            it.preScale(-1f, 1f)
        }

        val options = BitmapFactory.Options().also {
            it.inJustDecodeBounds = false
        }

        Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true).also { bmp2 ->
            ByteArrayOutputStream().use { out ->
                bmp2.compress(Bitmap.CompressFormat.JPEG, 90, out)

                ByteArrayInputStream(out.toByteArray()).use { input ->
                    BitmapRegionDecoder.newInstance(input, true)?.let { decoder ->
                        return decoder.decodeRegion(faceRect, options)
                    }
                }
            }
        }

        return null
    }

    fun getDataChecksum(seqNo: Byte, cmd: Byte, dataList: List<Byte>): String {
        val list = ArrayList<Byte>()
        list.add(seqNo)
        list.add(cmd)

        val dataSizeHex = dataList.size.toString(16)
        val dataSizeAscii = dataSizeHex.toByteArray(Charsets.US_ASCII);
        list.addAll(dataSizeAscii.toList())
        list.addAll(dataList)

        return getDataChecksum(list)
    }

    fun getDataChecksum(list: List<Byte>): String {
        val sum = byteHexIntSum(list)

        val hexSum1 = ("0000" + sum.toString(16))
        val hexSum2 = hexSum1.substring(hexSum1.length - 4)

        val sec2byte = Hex.decodeHex(hexSum2.toCharArray())
        val b = sec2byte[sec2byte.size - 1]

        return String.format("%02x", (b.inv()) + 1).uppercase(Locale.ROOT)
    }

    fun getChecksum(list: List<Byte>, def: String = ""): String {
        try {
            return ascii2String(list, def)
        } catch (e: Exception) {
            return def
        }
    }


    private const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val TIME_FORMAT = "HH:mm:ss"

    fun getDateTimeStr(pattern: String = DATE_TIME_FORMAT): String {
        return getDateTimeStr(LocalDateTime.now(), pattern)
    }

    fun getDateTimeStr(dateTime: LocalDateTime, pattern: String = DATE_TIME_FORMAT): String {
        return DateTimeFormatter.ofPattern(pattern).format(dateTime)
    }

    fun getDateStr(pattern: String = DATE_FORMAT): String {
        return getDateStr(LocalDate.now(), pattern)
    }

    fun getDateStr(date: LocalDate, pattern: String = DATE_FORMAT): String {
        return DateTimeFormatter.ofPattern(pattern).format(date)
    }

    fun getTimeStr(pattern: String = TIME_FORMAT): String {
        return getTimeStr(LocalTime.now(), pattern)
    }

    fun getTimeStr(time: LocalTime, pattern: String = TIME_FORMAT): String {
        return DateTimeFormatter.ofPattern(pattern).format(time)
    }


    fun byteHexIntSum(list: List<Byte>): Int {
        var sum = 0
        for (b in list) {
            sum += byteHexIntValue(b)
        }
        return sum
    }

    fun byteHexIntValue(b: Byte): Int {
        val hex = String.format("%02x", b)
        val intValue = hex.toInt(16)
        return intValue
    }

    fun ascii2String(list: List<Byte>, def: String = ""): String {
        val data = ByteArray(list.size)
        if (list.isNotEmpty()) {
            for (i in list.indices) {
                data[i] = list[i]
            }
        }
        return ascii2String(data, def)
    }

    fun ascii2String(data: ByteArray, def: String = ""): String {
        try {
            return String(data, charset("US-ASCII"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return def
        }
    }

    fun string2Ascii(str: String): ByteArray {
        return str.toByteArray(Charsets.US_ASCII)
    }

    fun int2AsciiString(i: Int, separator: String = ""): String {
        return string2AsciiString(i.toString(), separator)
    }

    fun int2AsciiString(i: Int, size: Int, separator: String = ""): String {
        return string2AsciiString(String.format("%0${size}d", i), separator)
    }

    fun string2AsciiString(str: String, separator: String = ""): String {
        return str.toByteArray(Charsets.US_ASCII).joinToString(separator)
    }
}

// data class Prop(
//     private val id: Int,
//     private val value: String
// )

fun Bitmap.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(this.byteCount)
    this.copyPixelsToBuffer(buffer)
    return buffer.array()
}
