package com.example.pdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class LanguageSelectionActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
        // applyThemeAndColor() çağrısı buradan kaldırıldı. onCreate metoduna taşındı.
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
        applyThemeAndColor() // Temayı ayarlayan metod buraya taşındı.
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
        LocaleHelper.persist(this, languageCode)

        val intent: Intent
        if (SharedPreferencesManager.getUserName(this) == null) {
            intent = Intent(this, NameEntryActivity::class.java)
        } else {
            intent = Intent(this, MainActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}