package com.sandello.ndscalculator.feature.settings.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sandello.ndscalculator.feature.settings.SettingsRoute as SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute : NavKey

fun EntryProviderScope<NavKey>.settingsScreen(contentPadding: PaddingValues = PaddingValues()) {
    entry<SettingsRoute> {
        SettingsScreen(contentPadding)
    }
}
