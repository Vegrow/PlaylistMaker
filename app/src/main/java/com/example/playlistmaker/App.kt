package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {

    var darkThemeEnabled = false
        private set
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPreferences.getBoolean(APP_THEME_KEY, false))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        this.darkThemeEnabled = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferences.edit().putBoolean(APP_THEME_KEY, darkThemeEnabled).apply()
    }

    companion object {
        const val SHARED_PREFERENCES = "shared_preferences"
        const val APP_THEME_KEY = "app_theme_key"
    }
}