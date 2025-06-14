package com.sandello.ndscalculator.core.data.repository

import com.sandello.vatcalculator.VatCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class VatRepository @Inject constructor(
    private val vatCalculator: VatCalculator,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    val amount: Flow<String>
        get() = userPreferencesRepository.userPreferencesData.map { it.amount }

    val rate: Flow<String>
        get() = userPreferencesRepository.userPreferencesData.map { it.rate }

    val isSaveAmountEnabled: Flow<Boolean> = userPreferencesRepository.userPreferencesData.map { it.isSaveAmountEnabled }

    suspend fun setRate(rate: String) {
        userPreferencesRepository.setVatRate(rate)
        vatCalculator.rate = rate.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    suspend fun setAmount(amount: String) {
        userPreferencesRepository.setAmount(amount)
    }

    fun calculateVatAmount(amount: BigDecimal): BigDecimal {
        return vatCalculator.calculateVatAmount(amount)
    }

    fun calculateTotalWithVat(amount: BigDecimal): BigDecimal {
        return vatCalculator.calculateTotalWithVat(amount)
    }

    fun extractVatAmountFromTotal(amount: BigDecimal): BigDecimal {
        return vatCalculator.extractVatAmountFromTotal(amount)
    }
}
