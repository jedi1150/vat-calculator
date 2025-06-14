package com.sandello.vatcalculator

import android.util.Log
import java.math.BigDecimal
import java.math.RoundingMode

class VatCalculator(var rate: BigDecimal) {

    fun calculateVatAmount(amount: BigDecimal): BigDecimal {
        Log.d("VatCalculator", "calculateVatAmount: amount = $amount, rate = $rate")
        return amount.multiply(rate).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
    }

    fun calculateTotalWithVat(amount: BigDecimal): BigDecimal {
        Log.d("VatCalculator", "calculateTotalWithVat: amount = $amount, rate = $rate")
        return amount.add(calculateVatAmount(amount))
    }

    fun extractVatAmountFromTotal(totalAmount: BigDecimal): BigDecimal {
        Log.d("VatCalculator", "extractVatAmountFromTotal: totalAmount = $totalAmount, rate = $rate")
        return totalAmount.multiply(rate)
            .divide(rate.add(BigDecimal(100)), 2, RoundingMode.HALF_UP)
    }
}
