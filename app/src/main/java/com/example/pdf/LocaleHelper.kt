package com.example.pdf

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    // Call this in attachBaseContext of Activities
    fun onAttach(context: Context): Context {
        // Use stored language, or fallback to device default only if no language has EVER been selected by user
        // If a language was selected, it should always be used.
        val defaultSystemLanguage = Locale.getDefault().language
        val lang = SharedPreferencesManager.getLanguage(context) ?: defaultSystemLanguage
        return setLocale(context, lang)
    }

    // Call this when the language is actively changed by the user
    fun persist(context: Context, languageCode: String) {
        SharedPreferencesManager.saveLanguage(context, languageCode)
        SharedPreferencesManager.setLanguageSelected(context, true)
    }

    // Call this to apply the selected language and restart the activity
    fun applyLanguage(activity: Activity, languageCode: String) {
        persist(activity, languageCode)
        activity.recreate() // Recreate activity to apply new language
    }
}