package com.example.pdf

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import android.graphics.Color // Needed for default color constant

object SharedPreferencesManager {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_LANGUAGE_SELECTED_FLAG = "language_selected_flag"
    private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback_enabled"
    private const val KEY_TOUCH_SOUND = "touch_sound_enabled"
    private const val KEY_THEME = "theme_preference"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_PEN_COLOR = "pen_color"
    private const val KEY_PEN_SIZE_TYPE = "pen_size_type"
    private const val KEY_ERASER_SIZE_TYPE = "eraser_size_type"
    private const val KEY_SELECTED_APP_COLOR_THEME = "selected_app_color_theme" // YENİ: Tema renk seçimi

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserName(context: Context, name: String) {
        getPreferences(context).edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_NAME, null)
    }

    // NEW FUNCTIONS for Drawing preferences
    fun savePenColor(context: Context, color: Int) {
        getPreferences(context).edit().putInt(KEY_PEN_COLOR, color).apply()
    }

    fun getPenColor(context: Context): Int {
        return getPreferences(context).getInt(KEY_PEN_COLOR, Color.RED)
    }

    fun savePenSizeType(context: Context, sizeTypeOrdinal: Int) {
        getPreferences(context).edit().putInt(KEY_PEN_SIZE_TYPE, sizeTypeOrdinal).apply()
    }

    fun getPenSizeType(context: Context): Int {
        return getPreferences(context).getInt(KEY_PEN_SIZE_TYPE, DrawingModeType.MEDIUM.ordinal)
    }

    fun saveEraserSizeType(context: Context, sizeTypeOrdinal: Int) {
        getPreferences(context).edit().putInt(KEY_ERASER_SIZE_TYPE, sizeTypeOrdinal).apply()
    }

    fun getEraserSizeType(context: Context): Int {
        return getPreferences(context).getInt(KEY_ERASER_SIZE_TYPE, DrawingModeType.MEDIUM.ordinal)
    }

    // YENİ FONKSİYONLAR: Tema renk seçimi için
    fun saveAppColorTheme(context: Context, themeIndex: Int) {
        getPreferences(context).edit().putInt(KEY_SELECTED_APP_COLOR_THEME, themeIndex).apply()
    }

    fun getAppColorTheme(context: Context): Int {
        // Varsayılan olarak ilk renk temasını (mavi) döndür
        return getPreferences(context).getInt(KEY_SELECTED_APP_COLOR_THEME, 0)
    }

    // --- Existing Functions ---
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