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
     * 画像クォリティ
     */
    private const val IMAGE_QUALITY = 90

    /**
     * Drawableを画像に変換
     *
     * @param drawable Drawable
     * @return 画像
     */
    fun toBitmap(drawable: Drawable?): Bitmap {
        if (drawable == null) return Bitmap.createBitmap(0, 0, Config.ARGB_8888)

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
     *
     * @param image    画像
     * @param rotation 回転
     * @return 回転画像
     */
    fun flip(image: Bitmap, rotation: Int): Bitmap {
        val imageWidth: Int = image.width
        val imageHeight: Int = image.height

        val matrix = Matrix()
        matrix.setRotate(rotation.toFloat())

        return Bitmap.createBitmap(image, 0, 0, imageWidth, imageHeight, matrix, true)
    }

    /**
     * 画像をトリミング
     *
     * @param image  画像
     * @param width  トリミング幅
     * @param height トリミング高さ
     * @return トリミング画像
     */
    fun trim(image: Bitmap, width: Int, height: Int): Bitmap {
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
     *
     * @param image   画像
     * @param rect    範囲
     * @param quality クオリティ
     * @return 切り抜き画像
     */
    fun crop(image: Bitmap, rect: Rect, quality: Int = IMAGE_QUALITY): Bitmap? {
        val matrix = Matrix().also {
            it.preScale(-1f, 1f)
        }

        val options = BitmapFactory.Options().also {
            it.inJustDecodeBounds = false
        }

        try {
            Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true).also { bmp2 ->
                ByteArrayOutputStream().use { out ->
                    bmp2.compress(Bitmap.CompressFormat.JPEG, quality, out)

                    ByteArrayInputStream(out.toByteArray()).use { input ->
                        BitmapRegionDecoder.newInstance(input, true)?.let { decoder ->
                            return decoder.decodeRegion(rect, options)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            return null
        }

        return null
    }

    /**
     * 画像をBase64PNG文字列に変換
     *
     * @param image   画像
     * @param quality クオリティ
     * @return Base64PNG文字列
     */
    fun toBase64Png(image: Bitmap, quality: Int = IMAGE_QUALITY): String {
        return toBase64(image, Bitmap.CompressFormat.PNG, quality)
    }

    /**
     * 画像をBase64JPEG文字列に変換
     *
     * @param image   画像
     * @param quality クオリティ
     * @return Base64JPEG文字列
     */
    fun toBase64Jpeg(image: Bitmap, quality: Int = IMAGE_QUALITY): String {
        return toBase64(image, Bitmap.CompressFormat.JPEG, quality)
    }

    /**
     * 画像をBase64文字列に変換
     *
     * @param image   画像
     * @param format  フォーマット
     * @param quality クオリティ
     * @return Base64文字列
     */
    fun toBase64(image: Bitmap, format: Bitmap.CompressFormat, quality: Int = IMAGE_QUALITY): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return when (format) {
            Bitmap.CompressFormat.JPEG -> "data:image/jpeg;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"
            Bitmap.CompressFormat.PNG -> "data:image/png;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"
            else -> Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    /**
     * Base64文字列を画像に変換
     *
     * @param base64 Base64文字列
     * @return 画像
     */
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
     *
     * @return byte配列
     */
    fun toByteArray(image: Bitmap): ByteArray {
        val buffer = ByteBuffer.allocate(image.byteCount)
        image.copyPixelsToBuffer(buffer)
        return buffer.array()
    }
}
