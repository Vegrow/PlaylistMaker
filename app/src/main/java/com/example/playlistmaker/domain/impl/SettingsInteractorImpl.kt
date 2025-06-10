package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository,
) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean? = settingsRepository.isDarkThemeEnabled()

    override fun setDarkThemeEnabled(isDarkThemeEnabled: Boolean) =
        settingsRepository.setDarkThemeEnabled(isDarkThemeEnabled)
}