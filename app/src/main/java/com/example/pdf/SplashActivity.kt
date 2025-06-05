package com.example.pdf // Paket adını kontrol et

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

// R dosyasını import etmeyi unutma (gerekirse)
// import com.example.pdf.R

@SuppressLint("CustomSplashScreen") // Android 12+ splash API'si için uyarıyı bastırır, şimdilik basit tutuyoruz
class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIMEOUT: Long = 2000 // 2 saniye (milisaniye cinsinden)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Belirli bir süre sonra ana aktiviteye geç
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // SplashActivity'yi kapat, geri dönülmesin
        }, SPLASH_TIMEOUT)
    }
}