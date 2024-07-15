package com.yuruneji.cameratraining2.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Matrix
import android.graphics.Rect
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

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

}
