package com.sandello.ndscalculator.baselineprofile

import android.os.SystemClock
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObject2

val TARGET_PACKAGE: String
    get() = InstrumentationRegistry.getArguments().getString("targetAppId")
        ?: "com.sandello.ndscalculator"

val PROFILE_ITERATIONS: Int
    get() = InstrumentationRegistry.getArguments()
        .getString("baselineprofile.iterations")
        ?.toIntOrNull()
        ?.coerceIn(1, 15)
        ?: 3

fun MacrobenchmarkScope.startActivityAndWaitForCalculator() {
    startActivityAndWait()
    waitForAnyLabel(AMOUNT_LABELS)
}

fun MacrobenchmarkScope.openSettings() {
    clickLabeled(SETTINGS_LABELS)
    waitForAnyLabel(SETTINGS_CONTENT_LABELS)
}

fun MacrobenchmarkScope.openCalculator() {
    clickLabeled(CALCULATOR_LABELS)
    waitForAnyLabel(AMOUNT_LABELS)
}

private fun MacrobenchmarkScope.clickLabeled(labels: List<String>) {
    val node = awaitNode(labels)
    requireNotNull(node) { "Cannot find destination ${labels.joinToString()}" }.click()
    device.waitForIdle()
}

private fun MacrobenchmarkScope.waitForAnyLabel(labels: List<String>) {
    check(awaitNode(labels) != null) { "Timed out waiting for ${labels.joinToString()}" }
}

private fun MacrobenchmarkScope.awaitNode(labels: List<String>): UiObject2? {
    val deadline = SystemClock.elapsedRealtime() + CONTENT_TIMEOUT_MS
    while (SystemClock.elapsedRealtime() < deadline) {
        val node = labels.firstNotNullOfOrNull { label ->
            device.findObject(By.desc(label)) ?: device.findObject(By.text(label))
        }
        if (node != null) {
            return node
        }
        device.waitForIdle()
    }
    return null
}

private val AMOUNT_LABELS = listOf("Amount", "Сумма", "Сума", "Сома", "Miqdor")
private val SETTINGS_LABELS = listOf(
    "Settings",
    "Настройки",
    "Налады",
    "Баптаулар",
    "Налаштування",
    "Sozlamalar",
)
private val SETTINGS_CONTENT_LABELS = listOf("Theme", "Тема", "Тэма", "Mavzu")
private val CALCULATOR_LABELS = listOf(
    "Calculator",
    "Калькулятор",
    "Калькулятар",
    "Есептегіш",
    "Kalkulyator",
)

private const val CONTENT_TIMEOUT_MS = 5_000L
