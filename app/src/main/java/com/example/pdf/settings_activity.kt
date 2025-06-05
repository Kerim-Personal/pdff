package com.example.pdf // Paket adınızı kontrol edin

import android.content.Context // attachBaseContext için eklendi
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem // MenuItem için eklendi
import android.widget.LinearLayout // LinearLayout için eklendi
import android.content.Intent // Intent için eklendi
import android.widget.Toast // Toast için eklendi


class SettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase)) // Dil ayarlarını uygula
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // ActionBar'ı ayarla
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title) // Başlığı strings.xml'den al

        val layoutLanguageSettings: LinearLayout = findViewById(R.id.layoutLanguageSettings)
        layoutLanguageSettings.setOnClickListener {
            // TODO: Dil seçimi için LanguageSelectionActivity'yi yeniden kullanabilir veya yeni bir dialog gösterebiliriz.
            // Şimdilik LanguageSelectionActivity'ye gidelim.
            val intent = Intent(this, LanguageSelectionActivity::class.java)
            // LanguageSelectionActivity'den sonra MainActivity'ye değil, SettingsActivity'ye geri dönmesini sağlayabiliriz.
            // Veya LanguageSelectionActivity'nin her zaman MainActivity'ye yönlendirmesini kabul edebiliriz.
            // Şimdilik basit tutalım, mevcut davranışı koruyalım.
            startActivity(intent)
            // Eğer dil değişikliği sonrası SettingsActivity'nin de güncellenmesi gerekiyorsa,
            // LanguageSelectionActivity'den bir sonuç bekleyip recreate() çağrılabilir.
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // ActionBar'daki geri tuşuna basıldığında
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed() // Aktiviteyi sonlandır ve bir önceki aktiviteye dön
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}