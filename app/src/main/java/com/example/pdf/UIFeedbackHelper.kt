package com.example.pdf

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View

object UIFeedbackHelper {

    /**
     * Bir view'a tıklandığında, ayarlardan açıksa titreşim geri bildirimi verir.
     */
    fun provideFeedback(view: View) {
        val context = view.context
        if (SharedPreferencesManager.isHapticFeedbackEnabled(context)) {
            // Modern ve doğru titreşim servisi alma yöntemi
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            // minSdk 33 olduğu için eski sürüm kontrolüne gerek yok,
            // ancak en doğru yöntemle titreşimi tetikliyoruz.
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}