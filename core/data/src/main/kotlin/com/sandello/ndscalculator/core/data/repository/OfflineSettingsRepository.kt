package com.sandello.ndscalculator.core.data.repository

import com.sandello.ndscalculator.core.datastore.SettingsDataSource
import com.sandello.ndscalculator.core.model.SettingsData
import com.sandello.ndscalculator.core.model.ThemeType
import kotlinx.coroutines.flow.Flow
import java.util.Locale
import javax.inject.Inject

class OfflineSettingsRepository @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
) : SettingsRepository {
    override val settingsData: Flow<SettingsData> = settingsDataSource.settingsData

    override suspend fun setThemeType(themeType: ThemeType) = settingsDataSource.setThemeType(themeType)

    override suspend fun setLocale(locale: Locale) = settingsDataSource.setLocale(locale)

}
