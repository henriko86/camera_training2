package com.yuruneji.camera_training2.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Matrix
import android.graphics.Rect
import org.apache.commons.codec.binary.Hex
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import java.util.Locale
import kotlin.experimental.inv

/**
 * @author toru
 * @version 1.0
 */
object CommonUtil {

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
