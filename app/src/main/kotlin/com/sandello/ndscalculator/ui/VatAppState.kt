package com.sandello.ndscalculator.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import com.sandello.ndscalculator.feature.calculator.navigation.CalculatorRoute
import com.sandello.ndscalculator.feature.settings.navigation.SettingsRoute
import com.sandello.ndscalculator.navigation.NavigationState
import com.sandello.ndscalculator.navigation.Navigator
import com.sandello.ndscalculator.navigation.TopLevelDestination
import com.sandello.ndscalculator.navigation.rememberNavigationState

@Composable
fun rememberVatAppState(
    windowSizeClass: WindowSizeClass,
    startRoute: NavKey = CalculatorRoute,
    topLevelRoutes: Set<NavKey> = setOf(CalculatorRoute, SettingsRoute),
): VatAppState {
    val navigationState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = topLevelRoutes,
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    return remember(
        navigationState,
        navigator,
        windowSizeClass,
    ) {
        VatAppState(
            navigationState = navigationState,
            navigator = navigator,
            windowSizeClass = windowSizeClass,
        )
    }
}

@Stable
class VatAppState(
    val navigationState: NavigationState,
    val navigator: Navigator,
    private val windowSizeClass: WindowSizeClass,
) {
    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        navigator.navigate(topLevelDestination.key)
    }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
}
