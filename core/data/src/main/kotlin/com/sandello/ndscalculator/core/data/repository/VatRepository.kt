package com.sandello.ndscalculator.core.data.repository

import com.sandello.vatcalculator.VatCalculator
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VatRepository @Inject constructor(private val userPreferencesRepository: UserPreferencesRepository) {
    val amount: Flow<String>
        get() = userPreferencesRepository.userPreferencesData.map { it.amount }

    val rate: Flow<String>
        get() = userPreferencesRepository.userPreferencesData.map { it.rate }

    val isSaveAmountEnabled: Flow<Boolean> = userPreferencesRepository.userPreferencesData.map {
        it.isSaveAmountEnabled
    }

    suspend fun setRate(rate: String) {
        userPreferencesRepository.setVatRate(rate)
    }

    suspend fun setAmount(amount: String) {
        userPreferencesRepository.setAmount(amount)
    }

    fun calculateVatAmount(amount: BigDecimal, rate: BigDecimal): BigDecimal =
        VatCalculator.calculateVatAmount(amount, rate)

    fun calculateTotalWithVat(amount: BigDecimal, rate: BigDecimal): BigDecimal =
        VatCalculator.calculateTotalWithVat(amount, rate)

    fun extractVatAmountFromTotal(amount: BigDecimal, rate: BigDecimal): BigDecimal =
        VatCalculator.extractVatAmountFromTotal(amount, rate)
}
