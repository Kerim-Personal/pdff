package com.example.pdf

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.pdf.R

object ThemeManager {

    private val appColorThemes = listOf(
        "Serene Blue", "Red", "Green", "Purple", "Orange",
        "Deep Purple", "Indigo", "Cyan", "Pink", "Brown"
    )

    fun applyAppColorTheme(activity: Activity, themeIndex: Int) {
        SharedPreferencesManager.saveAppColorTheme(activity, themeIndex)
        activity.recreate()
    }

    fun getAppColorThemeCount(): Int {
        return appColorThemes.size
    }

    fun getAppColorThemeName(context: Context, themeIndex: Int): String {
        return when (themeIndex) {
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
    }

    fun applyCurrentThemeColors(context: Context) {
        // Bu metodun içeriği artık boş kalacak veya çağrılmayacak.
        // Tema seçimi ve uygulaması Activity'lerin onCreate metoduna taşındı.
    }
}