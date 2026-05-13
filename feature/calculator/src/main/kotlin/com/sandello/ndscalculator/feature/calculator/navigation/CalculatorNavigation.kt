package com.sandello.ndscalculator.feature.calculator.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sandello.ndscalculator.feature.calculator.CalculatorRoute
import kotlinx.serialization.Serializable

@Serializable
object CalculatorRoute

fun NavController.navigateToCalculator(navOptions: NavOptions? = null) {
    this.navigate(CalculatorRoute, navOptions)
}

fun NavGraphBuilder.calculatorScreen(
    contentPadding: PaddingValues = PaddingValues(),
) {
    composable<CalculatorRoute> {
        CalculatorRoute(contentPadding)
    }
}
