/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yuruneji.cameratraining2.common

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import android.util.Size
import android.view.TextureView
import kotlin.math.max

object CameraUtils {

    val TAG = "CameraUtils"


    /** Return the biggest preview size available which is smaller than the window */
    private fun findBestPreviewSize(windowSize: Size, characteristics: CameraCharacteristics):
            Size {
        val supportedPreviewSizes: List<Size> =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(SurfaceTexture::class.java)
                ?.filter { SizeComparator.compare(it, windowSize) >= 0 }
                ?.sortedWith(SizeComparator)
                ?: emptyList()

        return supportedPreviewSizes.getOrElse(0) { Size(0, 0) }
    }

    /**
     * Computes the relative rotation between the sensor orientation and the display rotation
     */
    private fun computeRelativeRotation(
        characteristics: CameraCharacteristics,
        deviceOrientationDegrees: Int
    ): Int {
        val sensorOrientationDegrees =
            characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

        // Reverse device orientation for front-facing cameras
        val sign = if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
            CameraCharacteristics.LENS_FACING_FRONT
        ) 1 else -1

        // Log.i(
        //     TAG,
        //     "(${sensorOrientationDegrees}-(${deviceOrientationDegrees} * ${sign}) + 360) % 360"
        // )

        if (sign == 1) {
            // Log.i(
            //     TAG,
            //     "前カメラ:${(sensorOrientationDegrees - (deviceOrientationDegrees * sign) + 360) % 360}"
            // )
        } else {
            // Log.i(
            //     TAG,
            //     "後ろカメラ:${(sensorOrientationDegrees - (deviceOrientationDegrees * sign) + 360) % 360}"
            // )
        }

        return (sensorOrientationDegrees - (deviceOrientationDegrees * sign) + 360) % 360
    }

    /**
     * Returns a new SurfaceTexture with optimized transformation
     */
    fun buildTargetTexture(
        containerView: TextureView,
        characteristics: CameraCharacteristics,
        surfaceRotation: Int
    ): SurfaceTexture? {

        Log.i(TAG, "")

        // Log.i(TAG, "画面向き=$surfaceRotation")
        // when (surfaceRotation) {
        //     Surface.ROTATION_0 -> Log.i(TAG, "画面向き=0度,$surfaceRotation")
        //     Surface.ROTATION_90 -> Log.i(TAG, "画面向き=90度,$surfaceRotation")
        //     Surface.ROTATION_180 -> Log.i(TAG, "画面向き=180度,$surfaceRotation")
        //     Surface.ROTATION_270 -> Log.i(TAG, "画面向き=270度,$surfaceRotation")
        // }

        val surfaceRotationDegrees = surfaceRotation * 90
        val windowSize = Size(containerView.width, containerView.height)
        val previewSize = findBestPreviewSize(windowSize, characteristics)
        val sensorOrientation =
            characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
        val isRotationRequired =
            computeRelativeRotation(characteristics, surfaceRotationDegrees) % 180 != 0

        Log.i(TAG, "画面向き=${surfaceRotationDegrees}度")
        Log.i(TAG, "画面サイズ=${windowSize.width},${windowSize.height}")
        Log.i(TAG, "カメラ画像サイズ=${previewSize.width},${previewSize.height}")
        Log.i(TAG, "カメラセンサー向き=${sensorOrientation}度")
        Log.i(TAG, "回転が必要か=$isRotationRequired")


        /* Scale factor required to scale the preview to its original size on the x-axis */
        var scaleX = 1f
        /* Scale factor required to scale the preview to its original size on the y-axis */
        var scaleY = 1f

        if (sensorOrientation == 0) {
            scaleX =
                if (!isRotationRequired) {
                    windowSize.width.toFloat() / previewSize.height
                } else {
                    windowSize.width.toFloat() / previewSize.width
                }

            scaleY =
                if (!isRotationRequired) {
                    windowSize.height.toFloat() / previewSize.width
                } else {
                    windowSize.height.toFloat() / previewSize.height
                }
        } else {
            scaleX =
                if (isRotationRequired) {
                    windowSize.width.toFloat() / previewSize.height
                } else {
                    windowSize.width.toFloat() / previewSize.width
                }

            scaleY =
                if (isRotationRequired) {
                    windowSize.height.toFloat() / previewSize.width
                } else {
                    windowSize.height.toFloat() / previewSize.height
                }
        }
        Log.i(TAG, "scaleX=${scaleX}, scaleY=${scaleY}")


        /* Scale factor required to fit the preview to the TextureView size */
        val finalScale = max(scaleX, scaleY)
        val halfWidth = windowSize.width / 2f
        val halfHeight = windowSize.height / 2f

        Log.i(TAG, "finalScale=${finalScale}")
        Log.i(TAG, "halfWidth=${halfWidth}, halfHeight=${halfHeight}")


        val matrix = Matrix()

        if (isRotationRequired) {
            matrix.setScale(
                1 / scaleX * finalScale,
                1 / scaleY * finalScale,
                halfWidth,
                halfHeight
            )

            Log.i(TAG, "縦向き: sx=${1 / scaleX * finalScale}, sy=${1 / scaleY * finalScale}")
        } else {
            matrix.setScale(
                windowSize.height / windowSize.width.toFloat() / scaleY * finalScale,
                windowSize.width / windowSize.height.toFloat() / scaleX * finalScale,
                halfWidth,
                halfHeight
            )

            val sx = windowSize.height / windowSize.width.toFloat() / scaleY * finalScale
            val sy = windowSize.width / windowSize.height.toFloat() / scaleX * finalScale

            Log.i(
                TAG,
                "横向き: sx=${sx}, sy=${sy}"
            )
        }

        // Rotate to compensate display rotation
        matrix.postRotate(
            -surfaceRotationDegrees.toFloat(),
            halfWidth,
            halfHeight
        )

        containerView.setTransform(matrix)


        Log.i(TAG, "")

        return containerView.surfaceTexture?.apply {
            setDefaultBufferSize(previewSize.width, previewSize.height)
        }
    }


    // /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    // private val cameraManager: CameraManager by lazy {
    //     getSystemService(Context.CAMERA_SERVICE) as CameraManager
    // }

    // /** [DisplayManager] to listen to display changes */
    // private val displayManager: DisplayManager by lazy {
    //     requireContext().getSystemService(DISPLAY_SERVICE) as DisplayManager
    // }

    fun getFrontCameraId(cameraManager: CameraManager): String? {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            when (facing) {
                CameraCharacteristics.LENS_FACING_FRONT -> return cameraId
                // CameraCharacteristics.LENS_FACING_BACK -> return cameraId
                // CameraCharacteristics.LENS_FACING_EXTERNAL -> return cameraId
            }
        }
        return null
    }

    fun getBackCameraId(cameraManager: CameraManager): String? {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            when (facing) {
                // CameraCharacteristics.LENS_FACING_FRONT -> return cameraId
                CameraCharacteristics.LENS_FACING_BACK -> return cameraId
                // CameraCharacteristics.LENS_FACING_EXTERNAL -> return cameraId
            }
        }
        return null
    }

}

internal object SizeComparator : Comparator<Size> {
    override fun compare(a: Size, b: Size): Int {
        return b.height * b.width - a.width * a.height
    }
}
