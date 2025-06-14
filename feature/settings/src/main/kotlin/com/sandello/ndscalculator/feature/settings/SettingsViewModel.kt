package com.sandello.ndscalculator.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandello.ndscalculator.core.data.repository.UserPreferencesRepository
import com.sandello.ndscalculator.core.model.UserPreferencesData
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
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> =
        userPreferencesRepository.userPreferencesData.map { userPreferencesData: UserPreferencesData ->
            SettingsUiState(
                themeType = userPreferencesData.themeType,
                locale = userPreferencesData.locale,
                isSaveAmountEnabled = userPreferencesData.isSaveAmountEnabled,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(
                themeType = ThemeType.SYSTEM,
                locale = Locale.ROOT,
                isSaveAmountEnabled = true,
            ),
        )

    fun updateThemeType(themeType: ThemeType) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeType(themeType)
        }
    }

    fun updateSaveAmount(isSaveAmountEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setSaveAmount(isSaveAmountEnabled)
        }
    }

    fun updateLocale(locale: Locale) {
        viewModelScope.launch {
            userPreferencesRepository.setLocale(locale)
        }
    }
}
