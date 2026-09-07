package com.sandello.ndscalculator.feature.calculator.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sandello.ndscalculator.feature.calculator.CalculatorRoute as CalculatorScreen
import kotlinx.serialization.Serializable

@Serializable
data object CalculatorRoute : NavKey

fun EntryProviderScope<NavKey>.calculatorScreen(contentPadding: PaddingValues = PaddingValues()) {
    entry<CalculatorRoute> {
        CalculatorScreen(contentPadding)
    }
}
