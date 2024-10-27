package com.sandello.ndscalculator.core.datastore

import androidx.datastore.core.DataStore
import java.util.Locale
import com.sandello.ndscalculator.core.model.ThemeType
import com.sandello.ndscalculator.core.model.SettingsData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataSource @Inject constructor(
    private val settingsDataStore: DataStore<Settings>,
) {
    val settingsData = settingsDataStore.data.map { settings ->
        SettingsData(
            themeType = when (settings.themeType) {
                ThemeTypeProto.SYSTEM -> ThemeType.SYSTEM
                ThemeTypeProto.LIGHT -> ThemeType.LIGHT
                ThemeTypeProto.DARK -> ThemeType.DARK
                ThemeTypeProto.UNRECOGNIZED -> ThemeType.SYSTEM
                null -> ThemeType.SYSTEM
            },
            locale = Locale(settings.languageTag),
        )
    }

    suspend fun setThemeType(themeType: ThemeType) {
        settingsDataStore.updateData { settings ->
            settings.copy {
                this.themeType = when (themeType) {
                    ThemeType.SYSTEM -> ThemeTypeProto.SYSTEM
                    ThemeType.LIGHT -> ThemeTypeProto.LIGHT
                    ThemeType.DARK -> ThemeTypeProto.DARK
                }
            }
        }
    }

    suspend fun setLocale(locale: Locale) {
        settingsDataStore.updateData { settings ->
            settings.copy {
                languageTag = locale.language
            }
        }
    }
}
