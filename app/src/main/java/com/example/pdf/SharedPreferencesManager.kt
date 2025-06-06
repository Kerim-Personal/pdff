package com.example.pdf

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object SharedPreferencesManager {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_LANGUAGE_SELECTED_FLAG = "language_selected_flag"
    private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback_enabled"
    private const val KEY_TOUCH_SOUND = "touch_sound_enabled"
    private const val KEY_THEME = "theme_preference"
    private const val KEY_USER_NAME = "user_name" // YENİ EKLENDİ: Kullanıcı adı için anahtar

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // YENİ FONKSİYONLAR EKLENDİ: Kullanıcı adını kaydetme ve okuma
    fun saveUserName(context: Context, name: String) {
        getPreferences(context).edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_NAME, null)
    }

    // --- Mevcut Fonksiyonlar ---
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

    fun setHapticFeedbackEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_HAPTIC_FEEDBACK, enabled).apply()
    }

    fun isHapticFeedbackEnabled(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_HAPTIC_FEEDBACK, true)
    }
    fun setTouchSoundEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_TOUCH_SOUND, enabled).apply()
    }

    fun isTouchSoundEnabled(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_TOUCH_SOUND, true)
    }

    fun saveTheme(context: Context, themeValue: Int) {
        getPreferences(context).edit().putInt(KEY_THEME, themeValue).apply()
    }

    fun getTheme(context: Context): Int {
        return getPreferences(context).getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}