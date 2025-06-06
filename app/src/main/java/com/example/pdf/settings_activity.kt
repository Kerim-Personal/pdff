package com.example.pdf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    private fun applyGlobalThemeAndColor() {
        val theme = SharedPreferencesManager.getTheme(this)
        AppCompatDelegate.setDefaultNightMode(theme)

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
        Log.d("ThemeDebug", "SettingsActivity - Tema uygulandı: ${resources.getResourceEntryName(themeResId)}, Gece Modu: $currentNightMode, Renk Teması: $selectedColorThemeIndex")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ThemeDebug", "SettingsActivity - onCreate çağrıldı.")
        applyGlobalThemeAndColor()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)

        val layoutLanguageSettings: LinearLayout = findViewById(R.id.layoutLanguageSettings)
        layoutLanguageSettings.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            val intent = Intent(this, LanguageSelectionActivity::class.java)
            startActivity(intent)
        }

        val layoutThemeSettings: LinearLayout = findViewById(R.id.layoutThemeSettings)
        layoutThemeSettings.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            showThemeDialog()
        }

        val layoutAppColorSettings: LinearLayout = findViewById(R.id.layoutAppColorSettings)
        layoutAppColorSettings.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            showAppColorDialog()
        }

        setupSwitches()
    }

    private fun setupSwitches() {
        val switchTouchSound: SwitchMaterial = findViewById(R.id.switchTouchSound)
        val switchHapticFeedback: SwitchMaterial = findViewById(R.id.switchHapticFeedback)

        switchTouchSound.isChecked = SharedPreferencesManager.isTouchSoundEnabled(this)
        switchHapticFeedback.isChecked = SharedPreferencesManager.isHapticFeedbackEnabled(this)

        switchTouchSound.setOnCheckedChangeListener { buttonView, isChecked ->
            UIFeedbackHelper.provideFeedback(buttonView)
            SharedPreferencesManager.setTouchSoundEnabled(this, isChecked)
        }

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
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                val selectedTheme = when (checkedItem) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                SharedPreferencesManager.saveTheme(this, selectedTheme)
                AppCompatDelegate.setDefaultNightMode(selectedTheme)
                recreate()
                setResult(Activity.RESULT_OK)
                Log.d("ThemeDebug", "SettingsActivity - Tema değişti: $selectedTheme, RESULT_OK ayarlandı.")
                dialog.dismiss()
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showAppColorDialog() {
        val themeColorNames = Array(ThemeManager.getAppColorThemeCount()) { i ->
            ThemeManager.getAppColorThemeName(this, i)
        }

        val currentAppColorThemeIndex = SharedPreferencesManager.getAppColorTheme(this)
        var checkedItem = currentAppColorThemeIndex

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.app_color_title))
            .setSingleChoiceItems(themeColorNames, checkedItem) { _, which ->
                checkedItem = which
            }
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                if (checkedItem != currentAppColorThemeIndex) {
                    ThemeManager.applyAppColorTheme(this, checkedItem)
                    setResult(Activity.RESULT_OK)
                    Log.d("ThemeDebug", "SettingsActivity - Renk teması değişti: $checkedItem, RESULT_OK ayarlandı.")
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_OK)
            Log.d("ThemeDebug", "SettingsActivity - Toolbar geri tuşu: RESULT_OK ayarlandı.")
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        Log.d("ThemeDebug", "SettingsActivity - Fiziksel geri tuşu: RESULT_OK ayarlandı.")
        super.onBackPressed()
    }
}