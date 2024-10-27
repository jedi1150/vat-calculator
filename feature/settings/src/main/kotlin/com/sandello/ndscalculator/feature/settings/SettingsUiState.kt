package com.sandello.ndscalculator.feature.settings

import com.sandello.ndscalculator.core.model.ThemeType
import java.util.Locale

data class SettingsUiState(
    var themeType: ThemeType = ThemeType.SYSTEM,
    var locale: Locale = Locale.ROOT,
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
