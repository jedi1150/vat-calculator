package com.sandello.vatcalculator

import java.math.BigDecimal
import java.math.RoundingMode

object VatCalculator {

    fun calculateVatAmount(amount: BigDecimal, rate: BigDecimal): BigDecimal {
        return amount.multiply(rate).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
    }

    fun calculateTotalWithVat(amount: BigDecimal, rate: BigDecimal): BigDecimal {
        return amount.add(calculateVatAmount(amount, rate))
    }

    fun extractVatAmountFromTotal(totalAmount: BigDecimal, rate: BigDecimal): BigDecimal {
        return totalAmount.multiply(rate)
            .divide(rate.add(BigDecimal(100)), 2, RoundingMode.HALF_UP)
    }
}
