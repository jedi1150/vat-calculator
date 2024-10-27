package com.sandello.vatcalculator

class VatCalculator(var rate: Double) {

    fun calculateVatAmount(amount: Double): Double {
        return amount * (rate / 100)
    }

    fun calculateTotalWithVat(amount: Double): Double {
        return amount + calculateVatAmount(amount)
    }

    fun extractVatAmountFromTotal(totalAmount: Double): Double {
        return totalAmount * (rate / (100 + rate))
    }
}
