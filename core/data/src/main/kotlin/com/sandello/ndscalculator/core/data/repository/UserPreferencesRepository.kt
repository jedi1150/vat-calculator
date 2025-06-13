package com.sandello.ndscalculator.core.data.repository

import com.sandello.ndscalculator.core.model.UserPreferencesData
import com.sandello.ndscalculator.core.model.ThemeType
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface UserPreferencesRepository {
    val userPreferencesData: Flow<UserPreferencesData>

    suspend fun setThemeType(themeType: ThemeType)

    suspend fun setSaveAmount(isSaveAmountEnabled: Boolean)

    suspend fun setSaveAmountInitialized(isSaveAmountInitialized: Boolean)

    suspend fun setLocale(locale: Locale)

    suspend fun setAmount(amount: String)

    suspend fun setVatRate(vatRate: String)

}
