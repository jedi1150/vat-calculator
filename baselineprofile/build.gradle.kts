import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.sandello.ndscalculator.baselineprofile"
    compileSdk = 37

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
        testInstrumentationRunnerArguments["baselineprofile.iterations"] =
            providers.gradleProperty("baselineProfile.iterations").orElse("3").get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    flavorDimensions += listOf("flavor-type")
    productFlavors {
        create("prod") { dimension = "flavor-type" }
        create("beta") { dimension = "flavor-type" }
        create("dev") { dimension = "flavor-type" }
    }

    targetProjectPath = ":app"

    testOptions {
        managedDevices {
            localDevices {
                create("pixel6Api34") {
                    device = "Pixel 6"
                    apiLevel = 34
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

androidComponents {
    beforeVariants { variantBuilder ->
        variantBuilder.enable = variantBuilder.flavorName == "prod"
    }
    onVariants { variant ->
        val applicationId = when (variant.flavorName) {
            "beta" -> "com.sandello.ndscalculator.beta"
            "dev" -> "com.sandello.ndscalculator.dev"
            else -> "com.sandello.ndscalculator"
        }
        variant.instrumentationRunnerArguments.put("targetAppId", applicationId)
        val buildType = variant.buildType.orEmpty()
        if (buildType.contains("nonMinified", ignoreCase = true)) {
            variant.instrumentationRunnerArguments.put("androidx.benchmark.enabledRules", "BaselineProfile")
            variant.instrumentationRunnerArguments.put(
                "notClass",
                "com.sandello.ndscalculator.baselineprofile.StartupBenchmark",
            )
        }
        if (buildType.contains("benchmark", ignoreCase = true)) {
            variant.instrumentationRunnerArguments.put("androidx.benchmark.enabledRules", "Macrobenchmark")
        }
    }
}

val useGmd = providers.gradleProperty("baselineProfile.useGmd").orElse("false").get() == "true"

baselineProfile {
    if (useGmd) {
        useConnectedDevices = false
        managedDevices += "pixel6Api34"
    } else {
        useConnectedDevices = true
    }
}

if (providers.gradleProperty("baselineProfile.uninstall").orElse("false").get() == "true") {
    val uninstallConflictingPackages =
        tasks.register<UninstallConflictingPackagesTask>("uninstallConflictingPackages") {
            group = "verification"
            description = "Uninstalls Play Store and leftover test packages so profiling APKs can install."
            adbPath.set(androidComponents.sdkComponents.adb.map { it.asFile.absolutePath })
        }

    tasks.matching { task ->
        task.name.startsWith("connected") && task.name.endsWith("AndroidTest")
    }.configureEach {
        dependsOn(uninstallConflictingPackages)
    }
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}
