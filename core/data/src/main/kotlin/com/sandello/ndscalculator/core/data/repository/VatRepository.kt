package com.sandello.ndscalculator.core.data.repository

import com.sandello.vatcalculator.VatCalculator
import javax.inject.Inject

class VatRepository @Inject constructor(
    private val vatCalculator: VatCalculator,
) {
    val rate: Double
        get() = vatCalculator.rate

    fun setRate(rate: Double) {
        vatCalculator.rate = rate
    }

    fun calculateVatAmount(amount: Double): Double {
        return vatCalculator.calculateVatAmount(amount)
    }

    fun calculateTotalWithVat(amount: Double): Double {
        return vatCalculator.calculateTotalWithVat(amount)
    }

    fun extractVatAmountFromTotal(totalAmount: Double): Double {
        return vatCalculator.extractVatAmountFromTotal(totalAmount)
    }
}
