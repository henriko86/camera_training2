package com.yuruneji.camera_training.presentation.camera.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.Rect
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.common.CommonUtil.drawableToBitmap

/**
 * @author toru
 * @version 1.0
 */
class DrawRectView(
    context: Context,
    private val surfaceView: SurfaceView,
    private val previewMatrix: Matrix,
    private val previewWidth: Int,
    private val previewHeight: Int,
    /** 左右反転 [true:反転, false:非反転] */
    private val isImageFlipped: Boolean = true
) {

    /** 枠画像 */
    private val rectImage: Bitmap = drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.face_rect))

    /** 枠画像サイズ */
    private val rectImageSize: Rect = Rect(0, 0, rectImage.width, rectImage.height)

    /** スケール幅オフセット */
    private var postScaleWidthOffset: Float = 0f

    /** スケール高さオフセット */
    private var postScaleHeightOffset: Float = 0f

    /** スケール */
    private var scaleFactor: Float = 0f

    fun draw(
        imageWidth: Int,
        imageHeight: Int,
        list: List<Rect>
    ) {
        setSetting(imageWidth, imageHeight)

        if (list.isEmpty()) {
            clearCanvas()
        } else {
            val canvas: Canvas = getCanvas() ?: return
            canvas.save()

            list.forEach { rect ->
                drawSingleView(canvas, convertRect(rect))
            }

            canvas.restore()
            unlockCanvasAndPost(canvas)
        }
    }

    private fun drawSingleView(canvas: Canvas, drawRect: Rect) {
        canvas.drawBitmap(rectImage, rectImageSize, drawRect, null)
    }

    private fun clearCanvas() {
        val canvas: Canvas = surfaceView.holder.lockCanvas() ?: return
        canvas.let {
            it.drawColor(0, PorterDuff.Mode.CLEAR)
            surfaceView.holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun convertRect(rect: Rect): Rect {
        val x = translateX(rect.centerX().toFloat())
        val y = translateY(rect.centerY().toFloat())

        // Calculate positions.
        val left = x - scale2(rect.width() / 2.0f)
        val top = y - scale2(rect.height() / 2.0f)
        val right = x + scale2(rect.width() / 2.0f)
        val bottom = y + scale2(rect.height() / 2.0f)

        return Rect(
            left.toInt(),
            top.toInt(),
            right.toInt(),
            bottom.toInt()
        )
    }

    private fun getCanvas(): Canvas? {
        val canvas: Canvas = surfaceView.holder.lockCanvas() ?: return null
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        canvas.setMatrix(previewMatrix)
        return canvas
    }

    private fun unlockCanvasAndPost(canvas: Canvas) {
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }

    private fun setSetting(
        imageWidth: Int,
        imageHeight: Int
    ) {
        val viewAspectRatio = previewWidth.toFloat() / previewHeight
        val imageAspectRatio: Float = imageWidth.toFloat() / imageHeight
        postScaleWidthOffset = 0f
        postScaleHeightOffset = 0f
        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically cropped to be displayed in this view.
            scaleFactor = previewWidth.toFloat() / imageWidth
            postScaleHeightOffset = ((previewWidth.toFloat() / imageAspectRatio - previewHeight) / 2)
        } else {
            // The image needs to be horizontally cropped to be displayed in this view.
            scaleFactor = previewHeight.toFloat() / imageHeight
            postScaleWidthOffset = ((previewHeight.toFloat() * imageAspectRatio - previewWidth) / 2)
        }
    }

    /**
     * Adjusts the x coordinate from the image's coordinate system to the view coordinate system.
     */
    private fun translateX(x: Float): Float {
        return if (isImageFlipped) {
            previewWidth - (scale2(x) - postScaleWidthOffset)
        } else {
            scale2(x) - postScaleWidthOffset
        }
    }

    /**
     * Adjusts the y coordinate from the image's coordinate system to the view coordinate system.
     */
    private fun translateY(y: Float): Float {
        return scale2(y) - postScaleHeightOffset
    }

    /**
     * Adjusts the supplied value from the image scale to the view scale.
     */
    private fun scale2(imagePixel: Float): Float {
        return imagePixel * scaleFactor
    }
}
