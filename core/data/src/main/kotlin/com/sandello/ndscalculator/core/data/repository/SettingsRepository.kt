package com.sandello.ndscalculator.core.data.repository

import com.sandello.ndscalculator.core.model.SettingsData
import com.sandello.ndscalculator.core.model.ThemeType
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface SettingsRepository {
    val settingsData: Flow<SettingsData>

    suspend fun setThemeType(themeType: ThemeType)

    suspend fun setLocale(locale: Locale)

}
