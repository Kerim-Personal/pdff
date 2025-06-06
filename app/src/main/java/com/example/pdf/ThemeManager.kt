package com.example.pdf

import android.app.Activity
import android.content.Context
import android.util.Log

object ThemeManager {

    private val appColorThemes = listOf(
        "Serene Blue", "Red", "Green", "Purple", "Orange",
        "Deep Purple", "Indigo", "Cyan", "Pink", "Brown"
    )

    fun applyAppColorTheme(activity: Activity, themeIndex: Int) {
        SharedPreferencesManager.saveAppColorTheme(activity, themeIndex)
        activity.recreate()
        Log.d("ThemeDebug", "ThemeManager - Renk teması uygulandı: $themeIndex")
    }

    fun getAppColorThemeCount(): Int {
        return appColorThemes.size
    }

    fun getAppColorThemeName(context: Context, themeIndex: Int): String {
        val themeName = when (themeIndex) {
            0 -> context.getString(R.string.theme_color_serene_blue)
            1 -> context.getString(R.string.theme_color_red)
            2 -> context.getString(R.string.theme_color_green)
            3 -> context.getString(R.string.theme_color_purple)
            4 -> context.getString(R.string.theme_color_orange)
            5 -> context.getString(R.string.theme_color_deep_purple)
            6 -> context.getString(R.string.theme_color_indigo)
            7 -> context.getString(R.string.theme_color_cyan)
            8 -> context.getString(R.string.theme_color_pink)
            9 -> context.getString(R.string.theme_color_brown)
            else -> context.getString(R.string.theme_color_unknown)
        }
        Log.d("ThemeDebug", "ThemeManager - Renk teması adı alındı: $themeName (index: $themeIndex)")
        return themeName
    }

    fun applyCurrentThemeColors(context: Context) {
        // Boş bırakılmış, tema uygulama Activity'lere taşındı
        Log.d("ThemeDebug", "ThemeManager - applyCurrentThemeColors çağrıldı (boş)")
    }
}