package com.sandello.vatcalculator

import java.math.BigDecimal
import java.math.RoundingMode

object VatCalculator {

    private val HUNDRED = BigDecimal(100)

    fun calculateVatAmount(amount: BigDecimal, rate: BigDecimal): BigDecimal =
        amount.multiply(rate).divide(HUNDRED, 2, RoundingMode.HALF_UP)

    fun calculateTotalWithVat(amount: BigDecimal, rate: BigDecimal): BigDecimal =
        amount.add(calculateVatAmount(amount, rate))

    fun extractVatAmountFromTotal(totalAmount: BigDecimal, rate: BigDecimal): BigDecimal = totalAmount.multiply(rate)
        .divide(rate.add(HUNDRED), 2, RoundingMode.HALF_UP)
}
