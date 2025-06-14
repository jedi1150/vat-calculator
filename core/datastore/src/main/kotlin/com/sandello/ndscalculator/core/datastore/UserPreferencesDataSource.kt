package com.sandello.ndscalculator.core.datastore

import androidx.datastore.core.DataStore
import java.util.Locale
import com.sandello.ndscalculator.core.model.UserPreferencesData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val userPreferencesDataStore: DataStore<UserPreferences>,
) {
    val userPreferencesData = userPreferencesDataStore.data.map { preferences ->
        UserPreferencesData(
            themeType = when (preferences.themeType) {
                ThemeTypeProto.SYSTEM -> com.sandello.ndscalculator.core.model.ThemeType.SYSTEM
                ThemeTypeProto.LIGHT -> com.sandello.ndscalculator.core.model.ThemeType.LIGHT
                ThemeTypeProto.DARK -> com.sandello.ndscalculator.core.model.ThemeType.DARK
                ThemeTypeProto.UNRECOGNIZED -> com.sandello.ndscalculator.core.model.ThemeType.SYSTEM
                null -> com.sandello.ndscalculator.core.model.ThemeType.SYSTEM
            },
            locale = Locale(preferences.languageTag),
            amount = preferences.amountValue,
            rate = preferences.vatValue,
            isSaveAmountEnabled = preferences.isSaveAmountEnabled,
            isSaveAmountInitialized = preferences.isSaveAmountInitialized,
        )
    }

    suspend fun setThemeType(themeType: com.sandello.ndscalculator.core.model.ThemeType) {
        userPreferencesDataStore.updateData { preferences ->
            preferences.copy {
                this.themeType = when (themeType) {
                    com.sandello.ndscalculator.core.model.ThemeType.SYSTEM -> ThemeTypeProto.SYSTEM
                    com.sandello.ndscalculator.core.model.ThemeType.LIGHT -> ThemeTypeProto.LIGHT
                    com.sandello.ndscalculator.core.model.ThemeType.DARK -> ThemeTypeProto.DARK
                }
            }
        }
    }

    suspend fun setSaveAmount(isSaveAmountEnabled: Boolean) {
        userPreferencesDataStore.updateData { preferences ->
            preferences.copy {
                this.isSaveAmountEnabled = isSaveAmountEnabled
            }
        }
    }

    suspend fun setSaveAmountInitialized(isSaveAmountInitialized: Boolean) {
        userPreferencesDataStore.updateData { preferences ->
            preferences.copy {
                this.isSaveAmountInitialized = isSaveAmountInitialized
            }
        }
    }

    suspend fun setLocale(locale: Locale) {
        userPreferencesDataStore.updateData { preferences ->
            preferences.copy {
                languageTag = locale.language
            }
        }
    }

    suspend fun setAmount(amount: String) {
        userPreferencesDataStore.updateData { preferences ->
            preferences.copy {
                amountValue = amount
            }
        }
    }

    suspend fun setVatRate(vatRate: String) {
        userPreferencesDataStore.updateData { preferences ->
            preferences.copy {
                vatValue = vatRate
            }
        }
    }
}
