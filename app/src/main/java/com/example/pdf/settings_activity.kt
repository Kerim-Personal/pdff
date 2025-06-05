package com.example.pdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)

        // XML'den view'ları bulma
        val layoutLanguageSettings: LinearLayout = findViewById(R.id.layoutLanguageSettings)
        val switchHapticFeedback: SwitchMaterial = findViewById(R.id.switchHapticFeedback)

        //--- SESLE İLGİLİ KODLAR KALDIRILDI ---

        // Mevcut dokunsal geri bildirim ayarını yükle ve switch'i ayarla
        switchHapticFeedback.isChecked = SharedPreferencesManager.isHapticFeedbackEnabled(this)

        // Dil ayarları tıklama dinleyicisi
        layoutLanguageSettings.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it) // Geri bildirim ekle
            val intent = Intent(this, LanguageSelectionActivity::class.java)
            startActivity(intent)
        }

        //--- SES SWITCH'İNİN TIKLAMA DİNLEYİCİSİ KALDIRILDI ---

        // Dokunsal geri bildirim switch'i için tıklama dinleyicisi
        switchHapticFeedback.setOnCheckedChangeListener { buttonView, isChecked ->
            UIFeedbackHelper.provideFeedback(buttonView) // Geri bildirim ekle
            SharedPreferencesManager.setHapticFeedbackEnabled(this, isChecked)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}