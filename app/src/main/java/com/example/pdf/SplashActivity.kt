package com.example.pdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIMEOUT: Long = 2000

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    private fun applyThemeAndColor() {
        val selectedColorThemeIndex = SharedPreferencesManager.getAppColorTheme(this)
        val currentNightMode = SharedPreferencesManager.getTheme(this)

        val themeResId = when (selectedColorThemeIndex) {
            0 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_SereneBlue_Dark else R.style.Theme_Pdf_SereneBlue_Light
            1 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_Red_Dark else R.style.Theme_Pdf_Red_Light
            2 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_Green_Dark else R.style.Theme_Pdf_Green_Light
            3 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_Purple_Dark else R.style.Theme_Pdf_Purple_Light
            4 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_Orange_Dark else R.style.Theme_Pdf_Orange_Light
            5 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_DeepPurple_Dark else R.style.Theme_Pdf_DeepPurple_Light
            6 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_Indigo_Dark else R.style.Theme_Pdf_Indigo_Light
            7 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_Cyan_Dark else R.style.Theme_Pdf_Cyan_Light
            8 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_Pink_Dark else R.style.Theme_Pdf_Pink_Light
            9 -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_Brown_Dark else R.style.Theme_Pdf_Brown_Light
            else -> if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) R.style.Theme_Pdf_SereneBlue_Dark else R.style.Theme_Pdf_SereneBlue_Light
        }
        setTheme(themeResId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyThemeAndColor()
        super.onCreate(savedInstanceState)

        // Durum çubuğunu gizleme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!SharedPreferencesManager.isLanguageSelected(this)) {
                startActivity(Intent(this, LanguageSelectionActivity::class.java))
            } else if (SharedPreferencesManager.getUserName(this) == null) {
                startActivity(Intent(this, NameEntryActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, SPLASH_TIMEOUT)
    }
}