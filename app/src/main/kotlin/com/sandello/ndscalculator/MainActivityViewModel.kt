package com.sandello.ndscalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandello.ndscalculator.core.data.repository.OfflineUserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    settingsRepository: OfflineUserPreferencesRepository,
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = settingsRepository.userPreferencesData.map { settingsData ->
        MainUiState.Success(settingsData)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    init {
        viewModelScope.launch {
            if (settingsRepository.userPreferencesData.first().isSaveAmountInitialized.not()) {
                settingsRepository.setSaveAmount(true)
                settingsRepository.setSaveAmountInitialized(true)
            }
        }
    }
}
