package com.sandello.ndscalculator.feature.calculator.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sandello.ndscalculator.feature.calculator.CalculatorRoute

const val calculatorRoute = "calculator"

fun NavController.navigateToCalculator(navOptions: NavOptions? = null) {
    this.navigate(calculatorRoute, navOptions)
}

fun NavGraphBuilder.calculatorScreen(
    contentPadding: PaddingValues = PaddingValues(),
) {
    composable(
        route = calculatorRoute,
    ) {
        CalculatorRoute(contentPadding)
    }
}
