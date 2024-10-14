package com.yuruneji.camera_training.presentation.camera.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.common.CommonUtil.drawableToBitmap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author toru
 * @version 1.0
 */
class DrawRectView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

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

    private var previewWidth: Int = 0
    private var previewHeight: Int = 0
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var isFlipped: Boolean = true

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)

        executor = Executors.newSingleThreadExecutor()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        this.previewWidth = width
        this.previewHeight = height
        updateScale(imageWidth, imageHeight)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        executor.shutdown()
    }

    // fun setImageFlipped(isFlipped: Boolean) {
    //     this.isFlipped = isFlipped
    // }

    fun setImageSize(width: Int, height: Int) {
        this.imageWidth = width
        this.imageHeight = height
        updateScale(imageWidth, imageHeight)
    }

    fun draw(list: List<Rect>) {
        executor.submit {
            if (list.isEmpty()) {
                clearCanvas()
            } else {
                val canvas: Canvas = getCanvas() ?: return@submit
                canvas.save()

                list.forEach { rect ->
                    canvas.drawBitmap(rectImage, rectImageSize, convertRect(rect), null)
                }

                canvas.restore()
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun clearCanvas() {
        val canvas: Canvas = holder.lockCanvas() ?: return
        canvas.let {
            it.drawColor(0, PorterDuff.Mode.CLEAR)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun getCanvas(): Canvas? {
        val canvas: Canvas = holder.lockCanvas() ?: return null
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        return canvas
    }

    private fun convertRect(rect: Rect): Rect {
        val x = translateX(rect.centerX().toFloat())
        val y = translateY(rect.centerY().toFloat())

        // Calculate positions.
        val left = x - scale(rect.width() / 2.0f)
        val top = y - scale(rect.height() / 2.0f)
        val right = x + scale(rect.width() / 2.0f)
        val bottom = y + scale(rect.height() / 2.0f)

        return Rect(
            left.toInt(),
            top.toInt(),
            right.toInt(),
            bottom.toInt()
        )
    }

    private fun updateScale(imageWidth: Int, imageHeight: Int) {
        if (previewWidth > 0 && previewHeight > 0 && imageWidth > 0 && imageHeight > 0) {
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
    }

    /**
     * Adjusts the x coordinate from the image's coordinate system to the view coordinate system.
     */
    private fun translateX(x: Float): Float {
        return if (isFlipped) {
            previewWidth - (scale(x) - postScaleWidthOffset)
        } else {
            scale(x) - postScaleWidthOffset
        }
    }

    /**
     * Adjusts the y coordinate from the image's coordinate system to the view coordinate system.
     */
    private fun translateY(y: Float): Float {
        return scale(y) - postScaleHeightOffset
    }

    /**
     * Adjusts the supplied value from the image scale to the view scale.
     */
    private fun scale(imagePixel: Float): Float {
        return imagePixel * scaleFactor
    }
}
