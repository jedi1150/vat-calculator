package com.sandello.ndscalculator.feature.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sandello.ndscalculator.core.designsystem.theme.VatTheme
import java.text.DecimalFormat
import java.text.NumberFormat

private val DecimalKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Decimal,
    imeAction = ImeAction.None,
)
private val DecimalInputPattern = Regex("""^\d*[.,]?\d*$""")
private val ClearFabEnter = scaleIn(transformOrigin = TransformOrigin(0f, 0f)) +
    fadeIn() +
    expandIn(expandFrom = Alignment.TopStart)
private val ClearFabExit = scaleOut(transformOrigin = TransformOrigin(0f, 0f)) +
    fadeOut() +
    shrinkOut(shrinkTowards = Alignment.TopStart)

@Composable
fun CalculatorRoute(contentPadding: PaddingValues = PaddingValues(), viewModel: CalculatorViewModel = hiltViewModel()) {
    val calculatorUiState by viewModel.uiState.collectAsStateWithLifecycle()

    CalculatorScreen(
        contentPadding = contentPadding,
        calculatorUiState = calculatorUiState,
        onAmountChange = viewModel::setAmount,
        onRateChange = viewModel::setRate,
        onClearClick = viewModel::clearValues,
    )
}

@Composable
private fun CalculatorScreen(
    contentPadding: PaddingValues = PaddingValues(),
    calculatorUiState: CalculatorUiState,
    onAmountChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onClearClick: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            VatResults(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp),
                calculatorUiState = calculatorUiState,
            )
            CalculatorInputRow(
                contentPadding = contentPadding,
                calculatorUiState = calculatorUiState,
                onAmountChange = onAmountChange,
                onRateChange = onRateChange,
            )
        }
        ClearValuesButton(
            contentPadding = contentPadding,
            visible = calculatorUiState.hasData,
            onClick = onClearClick,
        )
    }
}

@Composable
private fun VatResults(modifier: Modifier = Modifier, calculatorUiState: CalculatorUiState) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        VatResultGroup(
            titleRes = R.string.gross_amount,
            firstValue = calculatorUiState.grossAmount,
            firstLabelRes = R.string.vat,
            secondValue = calculatorUiState.grossInclude,
            secondLabelRes = R.string.include_vat,
        )
        VatResultGroup(
            titleRes = R.string.net_amount,
            firstValue = calculatorUiState.netAmount,
            firstLabelRes = R.string.vat,
            secondValue = calculatorUiState.netInclude,
            secondLabelRes = R.string.without_vat,
        )
    }
}

@Composable
private fun VatResultGroup(
    titleRes: Int,
    firstValue: String,
    firstLabelRes: Int,
    secondValue: String,
    secondLabelRes: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.headlineSmall,
        )
        ReadOnlyDecimalField(value = firstValue, labelRes = firstLabelRes)
        ReadOnlyDecimalField(value = secondValue, labelRes = secondLabelRes)
    }
}

@Composable
private fun ReadOnlyDecimalField(value: String, labelRes: Int) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        label = { Text(stringResource(labelRes)) },
        keyboardOptions = DecimalKeyboardOptions,
        shape = MaterialTheme.shapes.medium,
    )
}

@Composable
private fun CalculatorInputRow(
    contentPadding: PaddingValues,
    calculatorUiState: CalculatorUiState,
    onAmountChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val locale = ConfigurationCompat.getLocales(configuration).get(0) ?: LocalLocale.current.platformLocale
    val numberInstance: DecimalFormat = remember(locale) {
        (NumberFormat.getInstance(locale) as DecimalFormat).apply {
            isGroupingUsed = false
            minimumFractionDigits = 2
        }
    }
    val placeholder = numberInstance.format(0.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .systemBarsPadding()
            .padding(bottom = contentPadding.calculateBottomPadding())
            .imePadding(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = calculatorUiState.amount,
            onValueChange = { value -> sanitizeDecimalInput(value)?.let(onAmountChange) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("calculator_amount"),
            label = { Text(stringResource(R.string.amount)) },
            placeholder = { Text(placeholder) },
            keyboardOptions = DecimalKeyboardOptions,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
        )
        OutlinedTextField(
            value = calculatorUiState.rate,
            onValueChange = { value -> sanitizeDecimalInput(value)?.let(onRateChange) },
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(max = 120.dp),
            label = {
                Text(
                    text = stringResource(R.string.rate),
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                )
            },
            placeholder = { Text(placeholder) },
            suffix = { Text("%") },
            keyboardOptions = DecimalKeyboardOptions,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
        )
    }
}

@Composable
private fun ClearValuesButton(contentPadding: PaddingValues, visible: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 16.dp, bottom = 64.dp)
            .padding(contentPadding)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.BottomEnd,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = ClearFabEnter,
            exit = ClearFabExit,
        ) {
            SmallFloatingActionButton(
                onClick = onClick,
                modifier = Modifier.padding(vertical = 8.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                )
            }
        }
    }
}

private fun sanitizeDecimalInput(value: String): String? {
    if (value.isEmpty() || value.matches(DecimalInputPattern)) {
        return value.replace(",", ".")
    }
    return null
}

@Preview
@Composable
private fun PreviewCalculatorScreen() {
    VatTheme {
        CalculatorScreen(
            calculatorUiState = CalculatorUiState(),
            onAmountChange = {},
            onRateChange = {},
            onClearClick = {},
        )
    }
}
