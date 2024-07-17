package com.yuruneji.cameratraining2.presentation.home.view

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * @author toru
 * @version 1.0
 */
class HogeSurfaceView(context: Context?) : SurfaceView(context), SurfaceHolder.Callback {
    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
}
