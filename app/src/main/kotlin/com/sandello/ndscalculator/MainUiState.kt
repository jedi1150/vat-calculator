package com.sandello.ndscalculator

import com.sandello.ndscalculator.core.model.SettingsData

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val settings: SettingsData = SettingsData()) : MainUiState
}
