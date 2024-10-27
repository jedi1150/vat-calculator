package com.sandello.ndscalculator.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.sandello.ndscalculator.navigation.TopLevelDestination
import com.sandello.ndscalculator.navigation.VatNavHost

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun VatApp(
    windowSizeClass: WindowSizeClass,
    appState: VatAppState = rememberVatAppState(
        windowSizeClass = windowSizeClass,
    ),
) {
    Scaffold(
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                NavigationBar {
                    appState.topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = appState.currentDestination.isTopLevelDestinationInHierarchy(
                                destination
                            ),
                            onClick = {
                                appState.navigateToTopLevelDestination(destination)
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = destination.iconId),
                                    contentDescription = null,
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = destination.titleTextId),
                                )
                            },
                        )
                    }
                }
            }
        },
    ) { contentPadding ->
        Row(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                ),
        ) {
            if (appState.shouldShowNavRail) {
                NavigationRail(
                    modifier = Modifier.padding(contentPadding),
                ) {
                    appState.topLevelDestinations.forEach { destination ->
                        NavigationRailItem(
                            selected = appState.currentDestination.isTopLevelDestinationInHierarchy(
                                destination
                            ),
                            onClick = {
                                appState.navigateToTopLevelDestination(destination)
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = destination.iconId),
                                    contentDescription = null,
                                )
                            },
                            label = {
                                Text(text = stringResource(id = destination.titleTextId))
                            },
                        )
                    }
                }
            }
            VatNavHost(
                contentPadding = contentPadding,
                appState = appState,
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) = this?.hierarchy?.any {
    it.route?.contains(destination.name, true) == true
} == true
