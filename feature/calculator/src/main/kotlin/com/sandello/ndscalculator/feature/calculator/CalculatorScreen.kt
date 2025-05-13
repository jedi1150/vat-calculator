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
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sandello.ndscalculator.core.designsystem.theme.VatTheme
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CalculatorRoute(
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: CalculatorViewModel = hiltViewModel(),
) {
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
    val numberInstance: DecimalFormat = NumberFormat.getInstance(Locale.getDefault()) as DecimalFormat
    numberInstance.isGroupingUsed = false
    numberInstance.minimumFractionDigits = 2

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.gross_amount),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    OutlinedTextField(
                        value = numberInstance.format(calculatorUiState.grossAmount),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = { Text(stringResource(R.string.vat)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.None,
                        ),
                        shape = MaterialTheme.shapes.medium,
                    )
                    OutlinedTextField(
                        value = calculatorUiState.grossInclude.toPlainString(),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = { Text(stringResource(R.string.include_vat)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.None,
                        ),
                        shape = MaterialTheme.shapes.medium,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.net_amount),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    OutlinedTextField(
                        value = calculatorUiState.netAmount.toPlainString(),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = { Text(stringResource(R.string.vat)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.None,
                        ),
                        shape = MaterialTheme.shapes.medium,
                    )
                    OutlinedTextField(
                        value = calculatorUiState.netInclude.toPlainString(),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = { Text(stringResource(R.string.without_vat)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.None,
                        ),
                        shape = MaterialTheme.shapes.medium,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .systemBarsPadding()
                    .padding(bottom = contentPadding.calculateBottomPadding())
                    .imePadding(), horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = calculatorUiState.amount,
                    onValueChange = { value ->
                        onAmountChange(value)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    label = { Text(stringResource(R.string.amount)) },
                    placeholder = { Text(numberInstance.format(0.0)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.None,
                    ),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
                OutlinedTextField(
                    value = calculatorUiState.rate,
                    onValueChange = { value ->
                        onRateChange(value)
                    },
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
                    placeholder = { Text(numberInstance.format(0.0)) },
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.None,
                    ),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 64.dp)
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.BottomEnd,
        ) {
            AnimatedVisibility(
                visible = calculatorUiState.hasData,
                enter = scaleIn(transformOrigin = TransformOrigin(0f, 0f)) + fadeIn() + expandIn(expandFrom = Alignment.TopStart),
                exit = scaleOut(transformOrigin = TransformOrigin(0f, 0f)) + fadeOut() + shrinkOut(shrinkTowards = Alignment.TopStart),
            ) {
                SmallFloatingActionButton(
                    onClick = onClearClick,
                    modifier = Modifier.padding(vertical = 8.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close), contentDescription = null
                    )
                }
            }
        }
    }
}

fun Double.toPlainString() = String.format(Locale.getDefault(), "%.2f", this)

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
