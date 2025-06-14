package com.sandello.ndscalculator.feature.settings

import com.sandello.ndscalculator.core.model.ThemeType
import java.util.Locale

data class SettingsUiState(
    var themeType: ThemeType,
    var locale: Locale,
    var isSaveAmountEnabled: Boolean
) {
    val availableLocales = listOf(
        Locale.ROOT,
        Locale("en"),
        Locale("be"),
        Locale("ru"),
        Locale("kk"),
        Locale("uk"),
        Locale("uz"),
    ).sortedBy { locale -> locale.getDisplayLanguage(locale) }
}
