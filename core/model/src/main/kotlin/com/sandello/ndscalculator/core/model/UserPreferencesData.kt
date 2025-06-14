package com.sandello.ndscalculator.core.model

import java.util.Locale

data class UserPreferencesData(
    val themeType: ThemeType,
    val locale: Locale,
    val amount: String,
    val rate: String,
    val isSaveAmountEnabled: Boolean,
    val isSaveAmountInitialized: Boolean,
)
