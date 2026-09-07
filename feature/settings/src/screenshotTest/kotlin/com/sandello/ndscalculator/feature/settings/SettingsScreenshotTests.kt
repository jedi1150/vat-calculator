package com.sandello.ndscalculator.feature.settings

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.sandello.ndscalculator.core.designsystem.theme.VatTheme
import com.sandello.ndscalculator.core.model.ThemeType
import java.util.Locale

private const val PHONE_DEVICE = "spec:width=411dp,height=891dp"

private val PreviewSettingsUiState = SettingsUiState(
    themeType = ThemeType.SYSTEM,
    locale = Locale.forLanguageTag("en"),
    isSaveAmountEnabled = true,
)

@PreviewTest
@Preview(
    name = "light",
    device = PHONE_DEVICE,
    showBackground = true,
    locale = "en",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
fun SettingsLightScreenshot() {
    VatTheme(darkTheme = false, dynamicColor = false) {
        SettingsScreen(
            settingsUiState = PreviewSettingsUiState,
            appVersion = "1.0.0 (1)",
            onChangeThemeType = {},
            onChangeLocale = {},
            onChangeSaveAmount = {},
            onLinkClicked = {},
        )
    }
}

@PreviewTest
@Preview(
    name = "dark",
    device = PHONE_DEVICE,
    showBackground = true,
    locale = "en",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
fun SettingsDarkScreenshot() {
    VatTheme(darkTheme = true, dynamicColor = false) {
        SettingsScreen(
            settingsUiState = PreviewSettingsUiState,
            appVersion = "1.0.0 (1)",
            onChangeThemeType = {},
            onChangeLocale = {},
            onChangeSaveAmount = {},
            onLinkClicked = {},
        )
    }
}
