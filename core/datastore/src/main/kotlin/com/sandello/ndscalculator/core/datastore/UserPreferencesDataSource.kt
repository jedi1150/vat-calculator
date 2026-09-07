package com.sandello.ndscalculator.core.datastore

import androidx.datastore.core.DataStore
import com.sandello.ndscalculator.core.model.ThemeType
import com.sandello.ndscalculator.core.model.UserPreferencesData
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class UserPreferencesDataSource @Inject constructor(private val userPreferencesDataStore: DataStore<UserPreferences>) {
    val userPreferencesData = userPreferencesDataStore.data.map { preferences ->
        UserPreferencesData(
            themeType = preferences.themeType.toThemeType(),
            locale = Locale(preferences.languageTag),
            amount = preferences.amountValue,
            rate = preferences.vatValue,
            isSaveAmountEnabled = preferences.isSaveAmountEnabled,
            isSaveAmountInitialized = preferences.isSaveAmountInitialized,
        )
    }

    suspend fun setThemeType(themeType: ThemeType) {
        userPreferencesDataStore.updateData { preferences ->
            preferences.copy {
                this.themeType = themeType.toProto()
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

private fun ThemeTypeProto?.toThemeType(): ThemeType = when (this) {
    ThemeTypeProto.LIGHT -> ThemeType.LIGHT

    ThemeTypeProto.DARK -> ThemeType.DARK

    ThemeTypeProto.SYSTEM,
    ThemeTypeProto.UNRECOGNIZED,
    null,
    -> ThemeType.SYSTEM
}

private fun ThemeType.toProto(): ThemeTypeProto = when (this) {
    ThemeType.SYSTEM -> ThemeTypeProto.SYSTEM
    ThemeType.LIGHT -> ThemeTypeProto.LIGHT
    ThemeType.DARK -> ThemeTypeProto.DARK
}
