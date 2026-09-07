package com.sandello.ndscalculator.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import com.sandello.ndscalculator.navigation.TopLevelDestination
import com.sandello.ndscalculator.navigation.VatNavHost

@Composable
fun VatApp(
    windowSizeClass: WindowSizeClass,
    appState: VatAppState = rememberVatAppState(windowSizeClass = windowSizeClass),
) {
    Scaffold(
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                VatBottomBar(appState)
            }
        },
    ) { contentPadding ->
        Row(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        ) {
            if (appState.shouldShowNavRail) {
                VatNavRail(appState)
            }
            VatNavHost(
                contentPadding = contentPadding,
                appState = appState,
            )
        }
    }
}

@Composable
private fun VatBottomBar(appState: VatAppState) {
    NavigationBar {
        appState.topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = destination.key == appState.navigationState.topLevelRoute,
                onClick = { appState.navigateToTopLevelDestination(destination) },
                icon = { DestinationIcon(destination) },
                label = { DestinationLabel(destination) },
            )
        }
    }
}

@Composable
private fun VatNavRail(appState: VatAppState) {
    NavigationRail {
        appState.topLevelDestinations.forEach { destination ->
            NavigationRailItem(
                selected = destination.key == appState.navigationState.topLevelRoute,
                onClick = { appState.navigateToTopLevelDestination(destination) },
                icon = { DestinationIcon(destination) },
                label = { DestinationLabel(destination) },
            )
        }
    }
}

@Composable
private fun DestinationIcon(destination: TopLevelDestination) {
    Icon(
        painter = painterResource(id = destination.iconId),
        contentDescription = null,
    )
}

@Composable
private fun DestinationLabel(destination: TopLevelDestination) {
    Text(text = stringResource(id = destination.titleTextId))
}
