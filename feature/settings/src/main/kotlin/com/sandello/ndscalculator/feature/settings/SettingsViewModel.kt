package com.sandello.ndscalculator.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandello.ndscalculator.core.data.repository.SettingsRepository
import com.sandello.ndscalculator.core.model.SettingsData
import com.sandello.ndscalculator.core.model.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> =
        settingsRepository.settingsData.map { settingsData: SettingsData ->
            SettingsUiState(themeType = settingsData.themeType, locale = settingsData.locale)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun updateThemeType(themeType: ThemeType) {
        viewModelScope.launch {
            settingsRepository.setThemeType(themeType)
        }
    }

    fun updateLocale(locale: Locale) {
        viewModelScope.launch {
            settingsRepository.setLocale(locale)
        }
    }
}
