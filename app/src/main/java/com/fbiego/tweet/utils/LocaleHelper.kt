package com.fbiego.tweet.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, lang: String): Context {
        return updateResources(context, lang)
    }

    private fun updateResources(context: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    @Suppress("deprecation")
    private fun updateResourcesLegacy(context: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.locale = locale
        context.resources
            .updateConfiguration(configuration, context.resources.displayMetrics)
        return context
    }
}