package com.sandello.ndscalculator.feature.settings

import com.sandello.ndscalculator.core.model.ThemeType
import java.util.Locale

data class SettingsUiState(var themeType: ThemeType, var locale: Locale, var isSaveAmountEnabled: Boolean) {
    val availableLocales = listOf(
        Locale.ROOT,
        Locale.forLanguageTag("en"),
        Locale.forLanguageTag("be"),
        Locale.forLanguageTag("ru"),
        Locale.forLanguageTag("kk"),
        Locale.forLanguageTag("uk"),
        Locale.forLanguageTag("uz"),
    ).sortedBy { locale -> locale.getDisplayLanguage(locale) }
}
