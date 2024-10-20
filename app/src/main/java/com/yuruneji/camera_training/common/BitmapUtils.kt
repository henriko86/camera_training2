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
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * @author toru
 * @version 1.0
 */
object BitmapUtils {

    /**
     * Drawableを画像に変換
     * @param drawable Drawable
     * @return
     */
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

    /**
     * 画像を回転
     * @param source
     * @param rotation
     * @return
     */
    fun flipBitmap(source: Bitmap, rotation: Int): Bitmap {
        val imageWidth: Int = source.width
        val imageHeight: Int = source.height

        val matrix = Matrix()
        matrix.setRotate(rotation.toFloat())

        return Bitmap.createBitmap(
            source, 0, 0, imageWidth, imageHeight, matrix, true
        )
    }

    /**
     * 画像をトリミング
     * @param image
     * @param width
     * @param height
     * @return
     */
    fun bitmapTrim(image: Bitmap, width: Int, height: Int): Bitmap {
        val scale = if (image.width >= image.height) { // 横長
            width / image.width.toDouble()
        } else { // 縦長
            height / image.height.toDouble()
        }

        val dstWidth = (image.width * scale).toInt()
        val dstHeight = (image.height * scale).toInt()

        return Bitmap.createScaledBitmap(image, dstWidth, dstHeight, true)
    }

    /**
     * 画像を範囲で切り抜き
     * @param bmp
     * @param faceRect
     * @param quality
     * @return
     */
    fun faceClipping(bmp: Bitmap, faceRect: Rect, quality: Int = 90): Bitmap? {
        val matrix = Matrix().also {
            it.preScale(-1f, 1f)
        }

        val options = BitmapFactory.Options().also {
            it.inJustDecodeBounds = false
        }

        try {
            Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true).also { bmp2 ->
                ByteArrayOutputStream().use { out ->
                    bmp2.compress(Bitmap.CompressFormat.JPEG, quality, out)

                    ByteArrayInputStream(out.toByteArray()).use { input ->
                        BitmapRegionDecoder.newInstance(input, true)?.let { decoder ->
                            return decoder.decodeRegion(faceRect, options)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            return null
        }

        return null
    }

    fun toBase64Png(bitmap: Bitmap, quality: Int = 90): String {
        return toBase64(bitmap, Bitmap.CompressFormat.PNG, quality)
    }

    fun toBase64Jpeg(bitmap: Bitmap, quality: Int = 90): String {
        return toBase64(bitmap, Bitmap.CompressFormat.JPEG, quality)
    }

    fun toBase64(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int = 90): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return when (format) {
            Bitmap.CompressFormat.JPEG -> "data:image/jpeg;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"
            Bitmap.CompressFormat.PNG -> "data:image/png;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"
            else -> Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    fun toBitmap(base64: String): Bitmap? {
        try {
            val decodedBytes: ByteArray = Base64.decode(
                base64.substring(base64.indexOf(",") + 1),
                Base64.DEFAULT
            )
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Bitmapをbyte配列に変換
     * @return byte配列
     */
    fun toByteArray(bitmap: Bitmap): ByteArray {
        val buffer = ByteBuffer.allocate(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(buffer)
        return buffer.array()
    }
}
