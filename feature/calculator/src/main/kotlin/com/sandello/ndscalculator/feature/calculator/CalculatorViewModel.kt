package com.sandello.ndscalculator.feature.calculator

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandello.ndscalculator.core.data.repository.UserPreferencesRepository
import com.sandello.ndscalculator.core.data.repository.VatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val vatRepository: VatRepository,
    private val preferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<CalculatorUiState> = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val amount: String = if (preferencesRepository.userPreferencesData.first().isSaveAmountEnabled) vatRepository.amount.first() else String()
            val rate: String = vatRepository.rate.first()
            _uiState.update {
                it.copy(
                    amount = amount,
                    rate = rate,
                )
            }
            vatRepository.setAmount(amount)
            vatRepository.setRate(rate)
            count()
        }
    }

    fun setAmount(value: String) {
        _uiState.update {
            it.copy(amount = value)
        }
        viewModelScope.launch {
            vatRepository.setAmount(value)
            count()
        }
    }

    fun setRate(value: String) {
        _uiState.update {
            it.copy(rate = value)
        }
        viewModelScope.launch {
            vatRepository.setRate(value)
            count()
        }
    }

    fun clearValues() = _uiState.update {
        it.copy(
            amount = String(),
            grossAmount = String(),
            grossInclude = String(),
            netAmount = String(),
            netInclude = String(),
        )
    }

    private fun count() {
//        val amount: String = _uiState.value.amount
//        val rate: String = _uiState.value.rate

        val currentAmountBigDecimal: BigDecimal = _uiState.value.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val grossAmount: BigDecimal = vatRepository.calculateVatAmount(currentAmountBigDecimal)
        val grossInclude: BigDecimal = vatRepository.calculateTotalWithVat(currentAmountBigDecimal)
        val netAmount: BigDecimal = vatRepository.extractVatAmountFromTotal(currentAmountBigDecimal)
        val netInclude: BigDecimal = currentAmountBigDecimal.subtract(netAmount)

        Log.d("CalculatorViewModel", "currentAmountBigDecimal: $currentAmountBigDecimal")
        Log.d("CalculatorViewModel", "grossAmount: $grossAmount")
        Log.d("CalculatorViewModel", "grossInclude: $grossInclude")
        Log.d("CalculatorViewModel", "netAmount: $netAmount")
        Log.d("CalculatorViewModel", "netInclude: $netInclude")

        _uiState.update {
            it.copy(
                grossAmount = grossAmount.toString(),
                grossInclude = grossInclude.toString(),
                netAmount = netAmount.toString(),
                netInclude = netInclude.toString(),
            )
        }
//        } else {
//            _uiState.update {
//                it.copy(
//                    grossAmount = 0.0,
//                    grossInclude = 0.0,
//                    netAmount = 0.0,
//                    netInclude = 0.0,
//                )
//            }
//        }
    }

}

data class CalculatorUiState(
    val amount: String = String(),
    val rate: String = String(),
    val grossAmount: String = String(),
    val grossInclude: String = String(),
    val netAmount: String = String(),
    val netInclude: String = String(),
) {
    val hasData: Boolean
        get() = amount != String()
}

