package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {

    override fun isDarkThemeEnabled(): Boolean? {
        return if (sharedPreferences.contains(APP_THEME_KEY)) {
            sharedPreferences.getBoolean(APP_THEME_KEY, false)
        } else {
            null
        }
    }

    override fun setDarkThemeEnabled(isDarkThemeEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(APP_THEME_KEY, isDarkThemeEnabled).apply()
    }

    private companion object {
        const val APP_THEME_KEY = "app_theme_key"
    }
}