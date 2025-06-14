package com.sandello.ndscalculator

import com.sandello.ndscalculator.core.model.UserPreferencesData

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val settings: UserPreferencesData) : MainUiState
}
