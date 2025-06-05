package com.example.pdf

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_LANGUAGE_SELECTED_FLAG = "language_selected_flag"
    private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback_enabled" // Sadece bu kaldı

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveLanguage(context: Context, language: String) {
        getPreferences(context).edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(context: Context): String? {
        return getPreferences(context).getString(KEY_LANGUAGE, null)
    }

    fun setLanguageSelected(context: Context, selected: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_LANGUAGE_SELECTED_FLAG, selected).apply()
    }

    fun isLanguageSelected(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_LANGUAGE_SELECTED_FLAG, false)
    }

    // Dokunsal geri bildirim ayarları
    fun setHapticFeedbackEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_HAPTIC_FEEDBACK, enabled).apply()
    }

    fun isHapticFeedbackEnabled(context: Context): Boolean {
        // Varsayılan olarak titreşim açık gelsin
        return getPreferences(context).getBoolean(KEY_HAPTIC_FEEDBACK, true)
    }
}