package com.sandello.ndscalculator.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.sandello.ndscalculator.feature.calculator.navigation.navigateToCalculator
import com.sandello.ndscalculator.feature.settings.navigation.navigateToSettings
import com.sandello.ndscalculator.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberVatAppState(
    windowSizeClass: WindowSizeClass,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): VatAppState {
    return remember(
        navController,
        coroutineScope,
        windowSizeClass,
    ) {
        VatAppState(
            navController,
            windowSizeClass,
        )
    }
}

@Stable
class VatAppState(
    val navController: NavHostController,
    private val windowSizeClass: WindowSizeClass,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.CALCULATOR -> navController.navigateToCalculator(topLevelNavOptions)
            TopLevelDestination.SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
        }
    }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
}
