package com.yuruneji.camera_training.common.service

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author toru
 * @version 1.0
 */
class SoundObserver(
    private val context: Context
) : DefaultLifecycleObserver {

    // interface SoundListener {
    //     fun onPlay(soundId: SoundId)
    // }


    /** SoundPool */
    private var soundPool: SoundPool? = null

    /** 認証開始音 */
    private var authStartId = 0

    /** 認証成功音 */
    private var authSuccessId = 0

    /** 認証失敗音 */
    private var authErrorId = 0

    /** Handler */
    private lateinit var handler: Handler

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        val soundHandler = HandlerThread(SoundObserver::class.simpleName)
        soundHandler.start()
        handler = Handler(soundHandler.looper)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(3)
            .build()

        soundPool?.let {
            authStartId = it.load(context, com.yuruneji.camera_training.R.raw.confirm2, 1)
            authSuccessId = it.load(context, com.yuruneji.camera_training.R.raw.confirm53, 1)
            authErrorId = it.load(context, com.yuruneji.camera_training.R.raw.beep1, 1)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        soundPool?.autoResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)

        soundPool?.autoPause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        soundPool?.release()
        soundPool = null
    }

    /**
     * 認証開始音を再生
     */
    fun playAuthStart() {
        handler.post {
            soundPool?.play(authStartId, 1.0f, 1.0f, 0, 0, 1.0f)
        }
    }

    /**
     * 認証成功音を再生
     */
    fun playAuthSuccess() {
        handler.post {
            soundPool?.play(authSuccessId, 1.0f, 1.0f, 0, 0, 1.0f)
        }
    }

    /**
     * 認証失敗音を再生
     */
    fun playAuthError() {
        handler.post {
            soundPool?.play(authErrorId, 1.0f, 1.0f, 0, 0, 1.0f)
        }
    }

    /**
     * 音を再生
     */
    fun play(soundId: SoundId) {
        when (soundId) {
            SoundId.AUTH_START -> playAuthStart()
            SoundId.AUTH_SUCCESS -> playAuthSuccess()
            SoundId.AUTH_ERROR -> playAuthError()
        }
    }
}

enum class SoundId(val id: Int) {
    AUTH_START(0),
    AUTH_SUCCESS(1),
    AUTH_ERROR(2)
}
