package com.example.pdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class LanguageSelectionActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        val layoutTurkish: LinearLayout = findViewById(R.id.layoutTurkish)
        val layoutEnglish: LinearLayout = findViewById(R.id.layoutEnglish)

        layoutTurkish.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            setLanguageAndProceed("tr")
        }

        layoutEnglish.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            setLanguageAndProceed("en")
        }
    }

    private fun setLanguageAndProceed(languageCode: String) {
        LocaleHelper.persist(this, languageCode) // Seçilen dili kaydet

        // GÜNCELLENEN MANTIK:
        // Yönlendirme yapmadan önce kullanıcının adının kayıtlı olup olmadığını kontrol et.
        val intent: Intent
        if (SharedPreferencesManager.getUserName(this) == null) {
            // İsim kayıtlı değilse, isim girme ekranına git.
            intent = Intent(this, NameEntryActivity::class.java)
        } else {
            // İsim zaten kayıtlıysa, doğrudan ana ekrana git.
            intent = Intent(this, MainActivity::class.java)
        }

        // ÖNEMLİ: Buradaki intent flag'lerini yeniden gözden geçiriyoruz.
        // Eski, daha stabil olan FLAG_ACTIVITY_NEW_TASK ve FLAG_ACTIVITY_CLEAR_TASK bayrakları kalmalı.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // LanguageSelectionActivity'yi kapat
    }
}