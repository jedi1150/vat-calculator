package com.sandello.ndscalculator.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.sandello.ndscalculator.feature.calculator.navigation.calculatorRoute
import com.sandello.ndscalculator.feature.calculator.navigation.calculatorScreen
import com.sandello.ndscalculator.feature.settings.navigation.settingsScreen
import com.sandello.ndscalculator.ui.VatAppState

@Composable
fun VatNavHost(
    contentPadding: PaddingValues,
    appState: VatAppState,
    startDestination: String = calculatorRoute,
) {
    NavHost(
        navController = appState.navController,
        startDestination = startDestination,
    ) {
        calculatorScreen(contentPadding)
        settingsScreen(contentPadding)
    }
}
