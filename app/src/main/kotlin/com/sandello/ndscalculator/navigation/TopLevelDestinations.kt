package com.sandello.ndscalculator.navigation

import com.sandello.ndscalculator.R

enum class TopLevelDestination(
    val iconId: Int,
    val titleTextId: Int,
) {
    CALCULATOR(
        iconId = R.drawable.ic_percentage,
        titleTextId = R.string.screen_calculator,
    ),
    SETTINGS(
        iconId = R.drawable.ic_settings,
        titleTextId = R.string.screen_settings,
    ),
}
