package com.sandello.ndscalculator.core.model

import java.util.Locale

data class SettingsData(
    val themeType: ThemeType = ThemeType.SYSTEM,
    val locale: Locale = Locale.ROOT,
)
