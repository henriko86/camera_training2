package com.yuruneji.cameratraining2.domain.usecase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import android.util.Size
import android.view.Display
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity.DISPLAY_SERVICE
import androidx.core.app.ActivityCompat
import com.yuruneji.cameratraining2.domain.model.CameraData
import timber.log.Timber
import java.lang.Thread.currentThread
import java.nio.ByteBuffer
import java.util.concurrent.Executors


/**
 * @author toru
 * @version 1.0
 */
class Camera2Controller(
    private var mContext: Context,
    private var mTextureView: TextureView,
    private var mWidth: Int,
    private var mHeight: Int,
    private var mCameraId: String,
    private var mCallabck: Callback
) {

    interface Callback {
        fun onComplete(cameraData: CameraData)
        fun onFailure(throwable: Throwable)
    }

    private val mainHandler: Handler = Handler(mContext.mainLooper)

    private val displayManager: DisplayManager by lazy {
        mContext.getSystemService(DISPLAY_SERVICE) as DisplayManager
    }
    private val cameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("MyCameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)


    private var mCameraDevice: CameraDevice? = null
    private var mSurface: Surface? = null
    private var mImageReader: ImageReader? = null

    private var frontCameraAvailable = false
    private var backCameraAvailable = false


    private val frontCameraId = cameraManager.cameraIdList.firstOrNull {
        val facing =
            cameraManager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING)
        facing == CameraCharacteristics.LENS_FACING_FRONT
    }

    private val backCameraId = cameraManager.cameraIdList.firstOrNull {
        val facing =
            cameraManager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING)
        facing == CameraCharacteristics.LENS_FACING_BACK
    }


    // private var mHandlerThread: HandlerThread? = null
    // private var mHandler: Handler? = null
    // private var mSemaphores: Semaphore = Semaphore(1)
    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            Timber.i("onSurfaceTextureAvailable() width: $width, height: $height")
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            Timber.i("onSurfaceTextureSizeChanged() width: $width, height: $height")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            Timber.i("onSurfaceTextureDestroyed()")
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            // Timber.i("onSurfaceTextureUpdated()")
        }
    }
    private val mCameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Timber.i("onOpened() [${getThreadName()}]")
            mCameraDevice = camera
            try {
                mainHandler.post {
                    if (mTextureView.isAvailable) {
                        createCameraPreviewSession(camera)
                    }
                }
            } catch (t: Throwable) {
                release()
                Timber.e(t)
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            Timber.i("onDisconnected() [${getThreadName()}]")
            mCameraDevice = camera
            release()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Timber.i("onError() error: $error [${getThreadName()}]")
            mCameraDevice = camera
        }
    }
    private val mOnImageAvailableListener =
        ImageReader.OnImageAvailableListener { reader ->
            try {
                val image = reader.acquireLatestImage() ?: return@OnImageAvailableListener

                val yuvBytes: ByteBuffer = this.imageToByteBuffer(image)


                // Convert YUV to RGB
                val rs = RenderScript.create(this.mContext)

                val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
                val allocationRgb = Allocation.createFromBitmap(rs, bitmap)

                val allocationYuv =
                    Allocation.createSized(
                        rs,
                        android.renderscript.Element.U8(rs),
                        yuvBytes.array().size
                    )
                allocationYuv.copyFrom(yuvBytes.array())

                val scriptYuvToRgb =
                    ScriptIntrinsicYuvToRGB.create(rs, android.renderscript.Element.U8_4(rs))
                scriptYuvToRgb.setInput(allocationYuv)
                scriptYuvToRgb.forEach(allocationRgb)

                allocationRgb.copyTo(bitmap)

                val w = bitmap.width
                val h = bitmap.height

                val m = Matrix()
                m.setRotate(-90F)

                val afterBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, false)

                // Release
                bitmap.recycle()

                allocationYuv.destroy()
                allocationRgb.destroy()
                rs.destroy()
            } catch (e: Exception) {
                mCallabck.onFailure(e)
            } finally {
                reader?.close()
            }
        }

    private fun imageToByteBuffer(image: Image): ByteBuffer {
        val crop: Rect = image.cropRect
        val width: Int = crop.width()
        val height: Int = crop.height()

        val planes = image.planes
        val rowData = ByteArray(planes[0].rowStride)
        val bufferSize = width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8
        val output = ByteBuffer.allocateDirect(bufferSize)

        var channelOffset = 0
        var outputStride = 0

        for (planeIndex in 0..2) {
            if (planeIndex == 0) {
                channelOffset = 0
                outputStride = 1
            } else if (planeIndex == 1) {
                channelOffset = width * height + 1
                outputStride = 2
            } else if (planeIndex == 2) {
                channelOffset = width * height
                outputStride = 2
            }

            val buffer = planes[planeIndex].buffer
            val rowStride = planes[planeIndex].rowStride
            val pixelStride = planes[planeIndex].pixelStride

            val shift = if ((planeIndex == 0)) 0 else 1
            val widthShifted = width shr shift
            val heightShifted = height shr shift

            buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))

            for (row in 0 until heightShifted) {
                val length: Int

                if (pixelStride == 1 && outputStride == 1) {
                    length = widthShifted
                    buffer[output.array(), channelOffset, length]
                    channelOffset += length
                } else {
                    length = (widthShifted - 1) * pixelStride + 1
                    buffer[rowData, 0, length]

                    for (col in 0 until widthShifted) {
                        output.array()[channelOffset] = rowData[col * pixelStride]
                        channelOffset += outputStride
                    }
                }

                if (row < heightShifted - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
        }

        return output
    }

    fun onResume() {
        mTextureView.surfaceTextureListener = mSurfaceTextureListener
    }

    fun onPause() {
        mTextureView.surfaceTextureListener = null
        release()
        cameraThread.quitSafely()
    }

    fun release() {
        try {
            mImageReader?.close()
            mSurface?.release()
            mCameraDevice?.close()
        } catch (t: Throwable) {
            Timber.e("Failed to release resources.", t)
        }
    }

    fun openCamera() {
        // val cameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (ActivityCompat.checkSelfPermission(
                    mContext, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            cameraManager.registerAvailabilityCallback(object :
                CameraManager.AvailabilityCallback() {
                override fun onCameraAvailable(cameraId: String) {
                    super.onCameraAvailable(cameraId)
                    Timber.i("onCameraAvailable() cameraId: $cameraId")
                    if (cameraId == frontCameraId) {
                        frontCameraAvailable = true
                    }
                    if (cameraId == backCameraId) {
                        backCameraAvailable = true
                    }
                }

                override fun onCameraUnavailable(cameraId: String) {
                    super.onCameraUnavailable(cameraId)
                    Timber.i("onCameraAvailable() cameraId: $cameraId")

                    if (cameraId == frontCameraId) {
                        frontCameraAvailable = false
                    }
                    if (cameraId == backCameraId) {
                        backCameraAvailable = false
                    }
                }
            }, null)

            cameraManager.openCamera(mCameraId, mCameraStateCallback, cameraHandler)
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }

    private fun createPreviewRequest(cameraDevice: CameraDevice): CaptureRequest {

        val surface = Surface(this.mTextureView.surfaceTexture)
        // val imageReader = ImageReader.newInstance(
        //     this.textureView.width,
        //     this.textureView.height,
        //     ImageFormat.JPEG,
        //     2
        // )

        // プレビューテクスチャの設定
        val previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(surface)
        previewRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        )

        return previewRequestBuilder.build()
    }

    @Throws(CameraAccessException::class)
    private fun createCameraPreviewSession(cameraDevice: CameraDevice) {
        Timber.i("createCameraPreviewSession() [${getThreadName()}]")
        if (!mTextureView.isAvailable) return

        // val cameraDevice = mCameraDevice

        val characteristics = cameraManager.getCameraCharacteristics(mCameraId)
        val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

        val jpegSizes = map?.getOutputSizes(ImageFormat.JPEG);
        val yuvSizes = map?.getOutputSizes(ImageFormat.YUV_420_888);


        val size = CameraUtils.findBestPreviewSize(
            Size(mTextureView.width, mTextureView.height),
            characteristics
        )

        jpegSizes?.let {
            mImageReader = ImageReader.newInstance(
                size.width,
                size.height,
                ImageFormat.YUV_420_888,
                2
            )
            mImageReader?.setOnImageAvailableListener(mOnImageAvailableListener, null)
        }


        // val largest = Collections.max(
        //     listOf(map?.getOutputSizes(ImageFormat.JPEG)),
        //     CompareSizesByArea()
        // )

        val sessionCallback = object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                try {
                    val captureRequest = mCameraDevice?.createCaptureRequest(
                        CameraDevice.TEMPLATE_PREVIEW
                    )
                    captureRequest?.addTarget(mSurface!!)
                    // captureRequest?.addTarget(mImageReader!!.surface)
                    session.setRepeatingRequest(
                        captureRequest?.build()!!, null, cameraHandler
                    )
                } catch (t: Throwable) {
                    Timber.e(t)
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Timber.w("onConfigureFailed()")
            }
        }

        val transformedTexture = CameraUtils.buildTargetTexture(
            mTextureView, cameraManager.getCameraCharacteristics(mCameraId),
            displayManager.getDisplay(Display.DEFAULT_DISPLAY).rotation
        )



        this.mSurface = Surface(transformedTexture)
        try {
            mCameraDevice?.createCaptureSession(
                listOf(mSurface, mImageReader!!.surface),
                sessionCallback,
                cameraHandler
            )
        } catch (t: Throwable) {
            Timber.e(t)
        }


        // プレビュー用のテクスチャ取得
        val texture = this.mTextureView.surfaceTexture

        val surface = Surface(texture)
        val imageReader = ImageReader.newInstance(
            this.mTextureView.width,
            this.mTextureView.height,
            ImageFormat.JPEG,
            2
        )

        val surfaces = listOf(surface, imageReader.surface)

        val type = SessionConfiguration.SESSION_REGULAR
        val configurations = surfaces.map { OutputConfiguration(it) }
        // val executor = this.activity.mainExecutor
        // val executor = Executors.newSingleThreadExecutor()
        val executor = Executors.newCachedThreadPool()

        val previewRequest = this.createPreviewRequest(cameraDevice)
        val callback = object: CameraCaptureSession.StateCallback() {
            override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                // 起動成功時に呼ばれる
                // this@CameraProcessor.cameraCaptureSession = cameraCaptureSession
                cameraCaptureSession.setRepeatingRequest(previewRequest, null, null)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                // 起動失敗時に呼ばれる
            }
        }

        // 起動
        val configuration = SessionConfiguration(type, configurations, executor, callback)
        cameraDevice.createCaptureSession(configuration)
    }

    private fun getThreadName(): String {
        return currentThread().name
    }

    internal object SizeComparator : Comparator<Size> {
        override fun compare(a: Size, b: Size): Int {
            return b.height * b.width - a.width * a.height
        }
    }

    class CompareSizesByArea : Comparator<Size?> {
        override fun compare(o1: Size?, o2: Size?): Int {
            if (o1 == null || o2 == null) return 0

            return java.lang.Long.signum(
                o1.width.toLong() * o1.height - o2.width.toLong() * o2.height
            )
        }
    }
}
