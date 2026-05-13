package com.sandello.ndscalculator.navigation

import com.sandello.ndscalculator.R
import com.sandello.ndscalculator.feature.calculator.navigation.CalculatorRoute
import com.sandello.ndscalculator.feature.settings.navigation.SettingsRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val iconId: Int,
    val titleTextId: Int,
    val route: KClass<*>,
) {
    CALCULATOR(
        iconId = R.drawable.ic_percentage,
        titleTextId = R.string.screen_calculator,
        route = CalculatorRoute::class,
    ),
    SETTINGS(
        iconId = R.drawable.ic_settings,
        titleTextId = R.string.screen_settings,
        route = SettingsRoute::class,
    ),
}
