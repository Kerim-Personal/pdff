package com.example.pdf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIMEOUT: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // YENİ EKLENEN KOD: Durum çubuğu rengini kod ile zorla ayarla
        // Bu, XML temasındaki olası bir sorunu geçersiz kılar.
        window.statusBarColor = ContextCompat.getColor(this, R.color.serene_blue_dark)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!SharedPreferencesManager.isLanguageSelected(this)) {
                // 1. Durum: Dil seçilmemişse -> Dil Seçim Ekranı
                startActivity(Intent(this, LanguageSelectionActivity::class.java))
            } else if (SharedPreferencesManager.getUserName(this) == null) {
                // 2. Durum: Dil seçilmiş ama isim girilmemişse -> İsim Girme Ekranı
                startActivity(Intent(this, NameEntryActivity::class.java))
            } else {
                // 3. Durum: Her ikisi de tamamsa -> Ana Ekran
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish() // SplashActivity'yi kapat
        }, SPLASH_TIMEOUT)
    }
}