package com.sandello.vatcalculator

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class VatCalculatorTest {

    @Test
    fun calculateVatAmount_twentyPercentOfHundred() {
        val result = VatCalculator.calculateVatAmount(bd("100"), bd("20"))
        assertDecimalEquals("20.00", result)
    }

    @Test
    fun calculateVatAmount_roundsHalfUpToTwoDecimals() {
        val result = VatCalculator.calculateVatAmount(bd("10.05"), bd("10"))
        assertDecimalEquals("1.01", result)
    }

    @Test
    fun calculateTotalWithVat_addsRoundedVat() {
        val result = VatCalculator.calculateTotalWithVat(bd("100"), bd("20"))
        assertDecimalEquals("120.00", result)
    }

    @Test
    fun extractVatAmountFromTotal_twentyPercentGross() {
        val result = VatCalculator.extractVatAmountFromTotal(bd("120"), bd("20"))
        assertDecimalEquals("20.00", result)
    }

    @Test
    fun extractVatAmountFromTotal_roundsHalfUpToTwoDecimals() {
        val result = VatCalculator.extractVatAmountFromTotal(bd("100"), bd("18"))
        assertDecimalEquals("15.25", result)
    }

    @Test
    fun extractVatAmountFromTotal_zeroRate() {
        val result = VatCalculator.extractVatAmountFromTotal(bd("100"), BigDecimal.ZERO)
        assertDecimalEquals("0.00", result)
    }

    @Test
    fun extractVatAmountFromTotal_rateMinusOneHundredThrows() {
        assertThrows(ArithmeticException::class.java) {
            VatCalculator.extractVatAmountFromTotal(bd("100"), bd("-100"))
        }
    }

    private fun bd(value: String): BigDecimal = BigDecimal(value)

    private fun assertDecimalEquals(expected: String, actual: BigDecimal) {
        assertEquals(0, actual.compareTo(BigDecimal(expected)), "expected $expected but was $actual")
    }
}
