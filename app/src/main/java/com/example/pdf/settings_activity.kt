package com.example.pdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
        applyTheme()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)

        // --- DÜZELTME 1: Dil Ayarları için eksik olan OnClickListener eklendi ---
        val layoutLanguageSettings: LinearLayout = findViewById(R.id.layoutLanguageSettings)
        layoutLanguageSettings.setOnClickListener {
            // Tıklama geri bildirimi ver
            UIFeedbackHelper.provideFeedback(it)
            // Dil seçimi aktivitesini başlat
            val intent = Intent(this, LanguageSelectionActivity::class.java)
            startActivity(intent)
        }

        val layoutThemeSettings: LinearLayout = findViewById(R.id.layoutThemeSettings)
        layoutThemeSettings.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            showThemeDialog()
        }

        // --- DÜZELTME 2: Switch'ler için eksik olan mantık eklendi ---
        setupSwitches()
    }

    private fun setupSwitches() {
        val switchTouchSound: SwitchMaterial = findViewById(R.id.switchTouchSound)
        val switchHapticFeedback: SwitchMaterial = findViewById(R.id.switchHapticFeedback)

        // Başlangıçta SharedPreferences'dan mevcut ayarları oku ve Switch'leri ayarla
        switchTouchSound.isChecked = SharedPreferencesManager.isTouchSoundEnabled(this)
        switchHapticFeedback.isChecked = SharedPreferencesManager.isHapticFeedbackEnabled(this)

        // Dokunma Sesi Switch'i için listener
        switchTouchSound.setOnCheckedChangeListener { buttonView, isChecked ->
            UIFeedbackHelper.provideFeedback(buttonView)
            SharedPreferencesManager.setTouchSoundEnabled(this, isChecked)
        }

        // Dokunsal Geribildirim Switch'i için listener
        switchHapticFeedback.setOnCheckedChangeListener { buttonView, isChecked ->
            UIFeedbackHelper.provideFeedback(buttonView)
            SharedPreferencesManager.setHapticFeedbackEnabled(this, isChecked)
        }
    }

    private fun showThemeDialog() {
        val themes = arrayOf(
            getString(R.string.theme_light),
            getString(R.string.theme_dark),
            getString(R.string.theme_system_default)
        )

        val currentTheme = SharedPreferencesManager.getTheme(this)
        var checkedItem = when (currentTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> 0
            AppCompatDelegate.MODE_NIGHT_YES -> 1
            else -> 2
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.theme_title))
            .setSingleChoiceItems(themes, checkedItem) { _, which ->
                checkedItem = which
            }
            .setPositiveButton("OK") { dialog, _ ->
                val selectedTheme = when (checkedItem) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                SharedPreferencesManager.saveTheme(this, selectedTheme)
                AppCompatDelegate.setDefaultNightMode(selectedTheme)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun applyTheme() {
        val theme = SharedPreferencesManager.getTheme(this)
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}