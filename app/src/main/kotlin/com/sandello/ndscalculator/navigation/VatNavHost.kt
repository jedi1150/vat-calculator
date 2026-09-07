package com.sandello.ndscalculator.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.sandello.ndscalculator.feature.calculator.navigation.calculatorScreen
import com.sandello.ndscalculator.feature.settings.navigation.settingsScreen
import com.sandello.ndscalculator.ui.VatAppState

@Composable
fun VatNavHost(
    contentPadding: PaddingValues,
    appState: VatAppState,
) {
    val entryProvider = entryProvider {
        calculatorScreen(contentPadding)
        settingsScreen(contentPadding)
    }

    val fadeThrough = fadeIn() togetherWith fadeOut()

    NavDisplay(
        entries = appState.navigationState.toEntries(entryProvider),
        modifier = Modifier.fillMaxSize(),
        onBack = { appState.navigator.goBack() },
        transitionSpec = { fadeThrough },
        popTransitionSpec = { fadeThrough },
        predictivePopTransitionSpec = { fadeThrough },
    )
}
