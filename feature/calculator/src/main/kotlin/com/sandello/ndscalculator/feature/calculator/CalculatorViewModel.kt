package com.sandello.ndscalculator.feature.calculator

import androidx.lifecycle.ViewModel
import com.sandello.ndscalculator.core.data.repository.VatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val vatRepository: VatRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<CalculatorUiState> = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun setAmount(value: Double) {
        _uiState.update {
            it.copy(amount = value)
        }
        count()
    }

    fun setRate(value: Double) {
        _uiState.update {
            it.copy(rate = value)
        }
        vatRepository.setRate(value)
        count()
    }

    fun clearValues() = _uiState.update {
        it.copy(
            amount = 0.0,
            grossAmount = 0.0,
            grossInclude = 0.0,
            netAmount = 0.0,
            netInclude = 0.0,
        )
    }

    private fun count() {
        val amount = _uiState.value.amount
        val rate = _uiState.value.rate

        if (amount.isNaN().not() && rate.isNaN().not()) {
            val grossAmount = vatRepository.calculateVatAmount(amount)
            val grossInclude = vatRepository.calculateTotalWithVat(amount)
            val netAmount = vatRepository.extractVatAmountFromTotal(amount)
            val netInclude = amount - netAmount

            _uiState.update {
                it.copy(
                    grossAmount = grossAmount,
                    grossInclude = grossInclude,
                    netAmount = netAmount,
                    netInclude = netInclude,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    grossAmount = 0.0,
                    grossInclude = 0.0,
                    netAmount = 0.0,
                    netInclude = 0.0,
                )
            }
        }
    }

}

data class CalculatorUiState(
    val amount: Double = 0.0,
    val rate: Double = 20.0,
    val grossAmount: Double = 0.0,
    val grossInclude: Double = 0.0,
    val netAmount: Double = 0.0,
    val netInclude: Double = 0.0,
) {
    val hasData: Boolean
        get() = amount != 0.0
}

