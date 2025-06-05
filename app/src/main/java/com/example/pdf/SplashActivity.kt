package com.example.pdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIMEOUT: Long = 2000 // 2 seconds

    // No need to override attachBaseContext here as Splash does not show localized content
    // before deciding where to go.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPreferencesManager.isLanguageSelected(this)) {
                // Language has been selected before, proceed to MainActivity
                // MainActivity's attachBaseContext will handle applying the saved locale
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // First launch or language not selected, go to LanguageSelectionActivity
                startActivity(Intent(this, LanguageSelectionActivity::class.java))
            }
            finish() // Close SplashActivity
        }, SPLASH_TIMEOUT)
    }
}