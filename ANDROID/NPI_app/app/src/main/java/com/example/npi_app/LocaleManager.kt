package com.example.npi_app

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {
    private const val PREFS_NAME = "locale_prefs"
    private const val LANGUAGE_KEY = "app_language"

    // Set default language
    fun setLocale(context: Context, language: String) {
        val locale = when(language){
            "en" -> Locale.ENGLISH
            "es" -> Locale("es", "ES")
            else -> Locale(language)
        }

            //Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val lang = language // if(language == "es") "es_ES" else language
        // Save language preference
        saveLanguage(context, lang)
    }

    fun applyLocale(context: Context) {
        val language = getSavedLanguage(context) ?: Locale.getDefault().language
        setLocale(context, language)
    }

    private fun saveLanguage(context: Context, language: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(LANGUAGE_KEY, language).apply()
    }

    fun getSavedLanguage(context: Context): String? {
        val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return preferences.getString(LANGUAGE_KEY, null)
    }
}
