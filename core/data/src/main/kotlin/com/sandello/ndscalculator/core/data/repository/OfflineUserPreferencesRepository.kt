package com.sandello.ndscalculator.core.data.repository

import com.sandello.ndscalculator.core.datastore.UserPreferencesDataSource
import com.sandello.ndscalculator.core.model.UserPreferencesData
import com.sandello.ndscalculator.core.model.ThemeType
import kotlinx.coroutines.flow.Flow
import java.util.Locale
import javax.inject.Inject

class OfflineUserPreferencesRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {
    override val userPreferencesData: Flow<UserPreferencesData> = userPreferencesDataSource.userPreferencesData

    override suspend fun setThemeType(themeType: ThemeType) = userPreferencesDataSource.setThemeType(themeType)

    override suspend fun setSaveAmount(isSaveAmountEnabled: Boolean) = userPreferencesDataSource.setSaveAmount(isSaveAmountEnabled)

    override suspend fun setSaveAmountInitialized(isSaveAmountInitialized: Boolean) = userPreferencesDataSource.setSaveAmountInitialized(isSaveAmountInitialized)

    override suspend fun setLocale(locale: Locale) = userPreferencesDataSource.setLocale(locale)

    override suspend fun setAmount(amount: String) = userPreferencesDataSource.setAmount(amount)

    override suspend fun setVatRate(vatRate: String) = userPreferencesDataSource.setVatRate(vatRate)

}
