package com.sandello.ndscalculator.feature.calculator

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.sandello.ndscalculator.core.designsystem.theme.VatTheme

private const val PHONE_DEVICE = "spec:width=411dp,height=891dp"

private val EmptyCalculatorUiState = CalculatorUiState()

private val FilledCalculatorUiState = CalculatorUiState(
    amount = "100",
    rate = "20",
    grossAmount = "20.00",
    grossInclude = "120.00",
    netAmount = "16.67",
    netInclude = "83.33",
)

@PreviewTest
@Preview(
    name = "empty-light",
    device = PHONE_DEVICE,
    showBackground = true,
    locale = "en",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
fun CalculatorEmptyLightScreenshot() {
    VatTheme(darkTheme = false, dynamicColor = false) {
        CalculatorScreen(
            calculatorUiState = EmptyCalculatorUiState,
            onAmountChange = {},
            onRateChange = {},
            onClearClick = {},
        )
    }
}

@PreviewTest
@Preview(
    name = "empty-dark",
    device = PHONE_DEVICE,
    showBackground = true,
    locale = "en",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
fun CalculatorEmptyDarkScreenshot() {
    VatTheme(darkTheme = true, dynamicColor = false) {
        CalculatorScreen(
            calculatorUiState = EmptyCalculatorUiState,
            onAmountChange = {},
            onRateChange = {},
            onClearClick = {},
        )
    }
}

@PreviewTest
@Preview(
    name = "filled-light",
    device = PHONE_DEVICE,
    showBackground = true,
    locale = "en",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
fun CalculatorFilledLightScreenshot() {
    VatTheme(darkTheme = false, dynamicColor = false) {
        CalculatorScreen(
            calculatorUiState = FilledCalculatorUiState,
            onAmountChange = {},
            onRateChange = {},
            onClearClick = {},
        )
    }
}
