package com.sandello.ndscalculator.feature.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandello.ndscalculator.core.data.repository.UserPreferencesRepository
import com.sandello.ndscalculator.core.data.repository.VatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val vatRepository: VatRepository,
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _amount = MutableStateFlow("")
    private val _rate = MutableStateFlow("")

    val uiState: StateFlow<CalculatorUiState> = combine(_amount, _rate) { amount, rate ->
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
        initialValue = CalculatorUiState()
    )

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferencesData.first()
            val initialAmount = if (prefs.isSaveAmountEnabled) vatRepository.amount.first() else ""
            val initialRate = vatRepository.rate.first()
            _amount.value = initialAmount
            _rate.value = initialRate
        }

        @OptIn(FlowPreview::class)
        _amount
            .debounce(500)
            .onEach { vatRepository.setAmount(it) }
            .launchIn(viewModelScope)

        @OptIn(FlowPreview::class)
        _rate
            .debounce(500)
            .onEach { vatRepository.setRate(it) }
            .launchIn(viewModelScope)
    }

    fun setAmount(value: String) {
        _amount.value = value
    }

    fun setRate(value: String) {
        _rate.value = value
    }

    fun clearValues() {
        _amount.value = ""
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
