package com.sandello.ndscalculator.navigation

import androidx.navigation3.runtime.NavKey
import com.sandello.ndscalculator.R
import com.sandello.ndscalculator.feature.calculator.navigation.CalculatorRoute
import com.sandello.ndscalculator.feature.settings.navigation.SettingsRoute

enum class TopLevelDestination(val iconId: Int, val titleTextId: Int, val key: NavKey, val testTag: String) {
    CALCULATOR(
        iconId = R.drawable.ic_percentage,
        titleTextId = R.string.screen_calculator,
        key = CalculatorRoute,
        testTag = "calculator_tab",
    ),
    SETTINGS(
        iconId = R.drawable.ic_settings,
        titleTextId = R.string.screen_settings,
        key = SettingsRoute,
        testTag = "settings_tab",
    ),
}
