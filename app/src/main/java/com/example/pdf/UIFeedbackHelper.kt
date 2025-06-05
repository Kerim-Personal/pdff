package com.example.pdf

import android.content.Context
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View

object UIFeedbackHelper {

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0
    private var isSoundPoolLoaded = false

    fun init(context: Context) {
        if (soundPool == null) {
            soundPool = SoundPool.Builder().setMaxStreams(2).build()
            clickSoundId = soundPool!!.load(context, R.raw.click_sound, 1)
            soundPool!!.setOnLoadCompleteListener { _, _, status ->
                isSoundPoolLoaded = status == 0
            }
        }
    }

    private fun playClickSound(context: Context) {
        if (SharedPreferencesManager.isTouchSoundEnabled(context) && isSoundPoolLoaded) {

            soundPool?.play(clickSoundId, 0.2f, 0.2f, 1, 0, 1.0f)
        }
    }

    private fun performHapticFeedback(context: Context) {
        if (SharedPreferencesManager.isHapticFeedbackEnabled(context)) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    }

    fun provideFeedback(view: View) {
        val context = view.context
        playClickSound(context)
        performHapticFeedback(context)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        isSoundPoolLoaded = false
    }
}