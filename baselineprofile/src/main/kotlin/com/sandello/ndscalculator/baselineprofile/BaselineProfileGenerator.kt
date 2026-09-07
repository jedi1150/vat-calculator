package com.sandello.ndscalculator.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test

class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() {
        val iterations = PROFILE_ITERATIONS
        val stableIterations = minOf(2, iterations)
        baselineProfileRule.collect(
            packageName = TARGET_PACKAGE,
            maxIterations = iterations,
            stableIterations = stableIterations,
            includeInStartupProfile = true,
        ) {
            startActivityAndWaitForCalculator()
        }
        baselineProfileRule.collect(
            packageName = TARGET_PACKAGE,
            maxIterations = iterations,
            stableIterations = stableIterations,
            includeInStartupProfile = false,
        ) {
            startActivityAndWaitForCalculator()
            openSettings()
            openCalculator()
        }
    }
}
