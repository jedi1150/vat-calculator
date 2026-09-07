package com.sandello.ndscalculator.feature.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandello.ndscalculator.core.data.repository.UserPreferencesRepository
import com.sandello.ndscalculator.core.data.repository.VatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val vatRepository: VatRepository,
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val amountInput = MutableStateFlow("")
    private val rateInput = MutableStateFlow("")

    val uiState: StateFlow<CalculatorUiState> = combine(amountInput, rateInput) { amount, rate ->
        val amountBigDecimal = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val rateBigDecimal = rate.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val grossAmount = vatRepository.calculateVatAmount(amountBigDecimal, rateBigDecimal)
        val grossInclude = vatRepository.calculateTotalWithVat(amountBigDecimal, rateBigDecimal)
        val netAmount = vatRepository.extractVatAmountFromTotal(amountBigDecimal, rateBigDecimal)
        val netInclude = amountBigDecimal.subtract(netAmount)

        CalculatorUiState(
            amount = amount,
            rate = rate,
            grossAmount = grossAmount.toPlainString(),
            grossInclude = grossInclude.toPlainString(),
            netAmount = netAmount.toPlainString(),
            netInclude = netInclude.toPlainString(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalculatorUiState(),
    )

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferencesData.first()
            val initialAmount = if (prefs.isSaveAmountEnabled) vatRepository.amount.first() else ""
            val initialRate = vatRepository.rate.first()
            amountInput.value = initialAmount
            rateInput.value = initialRate
        }

        @OptIn(FlowPreview::class)
        amountInput
            .debounce(SAVE_DEBOUNCE_MS)
            .onEach { vatRepository.setAmount(it) }
            .launchIn(viewModelScope)

        @OptIn(FlowPreview::class)
        rateInput
            .debounce(SAVE_DEBOUNCE_MS)
            .onEach { vatRepository.setRate(it) }
            .launchIn(viewModelScope)
    }

    fun setAmount(value: String) {
        amountInput.value = value
    }

    fun setRate(value: String) {
        rateInput.value = value
    }

    fun clearValues() {
        amountInput.value = ""
    }

    private companion object {
        const val SAVE_DEBOUNCE_MS = 500L
    }
}

data class CalculatorUiState(
    val amount: String = "",
    val rate: String = "",
    val grossAmount: String = "",
    val grossInclude: String = "",
    val netAmount: String = "",
    val netInclude: String = "",
) {
    val hasData: Boolean
        get() = amount.isNotEmpty()
}
