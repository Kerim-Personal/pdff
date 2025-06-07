package com.example.pdf

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText

class NameEntryActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_name_entry) // Moved to here

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

        val editTextName: TextInputEditText = findViewById(R.id.editTextName)
        val buttonContinue: Button = findViewById(R.id.buttonContinue)

        buttonContinue.setOnClickListener {
            val name = editTextName.text.toString().trim()
            if (name.isNotEmpty()) {
                SharedPreferencesManager.saveUserName(this, name)
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_name_toast), Toast.LENGTH_SHORT).show()
            }
        }
    }
}