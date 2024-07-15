package com.yuruneji.cameratraining2.presentation.home.view

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.yuruneji.cameratraining2.domain.model.FaceDetectDetail
import timber.log.Timber

/**
 * @author toru
 * @version 1.0
 */
class DrawFaceView(
    // previewView: PreviewView,
    surfaceView: SurfaceView,
    drawable: Drawable
) {
    /** SurfaceView */
    private var mSurfaceView: SurfaceView = surfaceView

    /** 顔枠ビットマップ */
    private var mFaceRect: Bitmap

    /** 顔枠サイズ */
    private var mFaceRectRect: Rect

    private var cameraMatrix: Matrix = Matrix()
    private var cameraWidth: Int = 0
    private var cameraHeight: Int = 0

    private var isImageFlipped: Boolean = true
    private var postScaleWidthOffset: Float = 0f
    private var postScaleHeightOffset: Float = 0f
    private var scaleFactor: Float = 0f
    // var overlayWidth: Int = 0

    // var imageWidth: Int = 0
    // var imageHeight: Int = 0

    init {
        mFaceRect = drawableToBitmap(drawable)
        mFaceRectRect = Rect(0, 0, mFaceRect.width, mFaceRect.height)

        Timber.i("faceRectWidth: ${mFaceRect.width}, faceRectHeight: ${mFaceRect.height}")
    }

    // override fun surfaceCreated(holder: SurfaceHolder) {
    //     Timber.d("surfaceCreated")
    // }
    //
    // override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    //     Timber.d("surfaceChanged")
    // }
    //
    // override fun surfaceDestroyed(holder: SurfaceHolder) {
    //     Timber.d("surfaceDestroyed")
    // }

    /**
     * 顔枠表示
     * @param
     */
    fun drawFace(
        cameraMatrix: Matrix,
        cameraWidth: Int,
        cameraHeight: Int,
        imageWidth: Int,
        imageHeight: Int,
        faceDetectList: List<FaceDetectDetail>
    ) {
        this.cameraMatrix = cameraMatrix
        this.cameraWidth = cameraWidth
        this.cameraHeight = cameraHeight

        // Timber.i("cameraWidth: $cameraWidth, cameraHeight: $cameraHeight, imageWidth: $imageWidth, imageHeight: $imageHeight")

        if (faceDetectList.isEmpty()) {
            clearCanvas(mSurfaceView.holder)
        } else {
            val canvas: Canvas = mSurfaceView.holder.lockCanvas() ?: return
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            canvas.setMatrix(cameraMatrix)
            canvas.save()


            val viewAspectRatio = cameraWidth.toFloat() / cameraHeight
            val imageAspectRatio: Float = imageWidth.toFloat() / imageHeight
            postScaleWidthOffset = 0f
            postScaleHeightOffset = 0f
            if (viewAspectRatio > imageAspectRatio) {
                // The image needs to be vertically cropped to be displayed in this view.
                scaleFactor = cameraWidth.toFloat() / imageWidth
                postScaleHeightOffset =
                    ((cameraWidth.toFloat() / imageAspectRatio - cameraHeight) / 2)
            } else {
                // The image needs to be horizontally cropped to be displayed in this view.
                scaleFactor = cameraHeight.toFloat() / imageHeight
                postScaleWidthOffset =
                    ((cameraHeight.toFloat() * imageAspectRatio - cameraWidth) / 2)
            }

            faceDetectList.forEach { detail ->
                // Draws a circle at the position of the detected face, with the face's track id below.
                val x = translateX(detail.faceRect.centerX().toFloat())
                val y = translateY(detail.faceRect.centerY().toFloat())
                // canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)

                // Calculate positions.
                val left = x - scale2(detail.faceRect.width() / 2.0f)
                val top = y - scale2(detail.faceRect.height() / 2.0f)
                val right = x + scale2(detail.faceRect.width() / 2.0f)
                val bottom = y + scale2(detail.faceRect.height() / 2.0f)
                // val lineHeight = ID_TEXT_SIZE + BOX_STROKE_WIDTH
                // var yLabelOffset: Float = if (face.trackingId == null) 0f else -lineHeight

                val faceRect2 = Rect()
                faceRect2.top = top.toInt()
                faceRect2.bottom = bottom.toInt()
                faceRect2.left = left.toInt()
                faceRect2.right = right.toInt()

                drawSingleFace(canvas, faceRect2)
            }

            canvas.restore()
            mSurfaceView.holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawSingleFace(canvas: Canvas, faceRect: Rect) {
        val drawRect: Rect = getDrawRect(faceRect)

        canvas.drawBitmap(mFaceRect, mFaceRectRect, drawRect, null)
    }

    private fun clearCanvas(surfaceHolder: SurfaceHolder) {
        val canvas: Canvas = surfaceHolder.lockCanvas() ?: return
        canvas.let {
            it.drawColor(0, PorterDuff.Mode.CLEAR)
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap =
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun getDrawRect(faceRect: Rect): Rect {
        // val padding = 0
        val realFaceRect = Rect(faceRect)
        realFaceRect.top = ((faceRect.top))
        realFaceRect.left = ((faceRect.left))
        realFaceRect.right = ((faceRect.right))
        realFaceRect.bottom = ((faceRect.bottom))

        // val d = 0.0
        return Rect(
            realFaceRect.left,
            realFaceRect.top,
            realFaceRect.right,
            realFaceRect.bottom
        )
    }

    /**
     * Adjusts the x coordinate from the image's coordinate system to the view coordinate system.
     */
    private fun translateX(x: Float): Float {
        return if (isImageFlipped) {
            cameraWidth - (scale2(x) - postScaleWidthOffset)
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

    /** Adjusts the supplied value from the image scale to the view scale.  */
    private fun scale2(imagePixel: Float): Float {
        return imagePixel * scaleFactor
    }
}
