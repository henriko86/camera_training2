package com.yuruneji.camera_training.common

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool


/**
 * @author toru
 * @version 1.0
 */
class SoundManager (
    private val context: Context
) {
    private var soundPool: SoundPool? = null

    private var soundStartId = 0
    private var soundSuccessId = 0
    private var soundErrorId = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(3)
            .build()

        soundPool?.let {
            soundStartId = it.load(context, com.yuruneji.camera_training.R.raw.confirm2, 1)
            soundSuccessId = it.load(context, com.yuruneji.camera_training.R.raw.confirm53, 1)
            soundErrorId = it.load(context, com.yuruneji.camera_training.R.raw.beep1, 1)
        }
    }

    fun start() {
        soundPool?.play(soundStartId, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    fun success() {
        soundPool?.play(soundSuccessId, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    fun error() {
        soundPool?.play(soundErrorId, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
