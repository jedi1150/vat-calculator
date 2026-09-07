import org.gradle.api.attributes.Bundling
import org.gradle.language.base.plugins.LifecycleBasePlugin

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
}

val detektVersion = libs.versions.detekt.get()

val ktlint = configurations.create("ktlint") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    ktlint(libs.ktlint.cli) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

fun JavaExec.configureKtlintCli(format: Boolean) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    classpath = ktlint
    mainClass.set("io.github.ktlint.core.Main")
    workingDir = rootDir
    val reportFile = layout.buildDirectory.file("reports/ktlint/ktlint.xml")
    outputs.file(reportFile)
    doFirst {
        reportFile.get().asFile.parentFile.mkdirs()
    }
    args(
        buildList {
            if (format) {
                add("-F")
            }
            add("**/src/**/*.kt")
            add("**/*.kts")
            add("!**/build/**")
            add("--relative")
            add("--reporter=plain")
            add("--reporter=checkstyle,output=${reportFile.get().asFile.absolutePath}")
        },
    )
}

val ktlintCheck = tasks.register<JavaExec>("ktlintCheck") {
    description = "Check Kotlin code style with ktlint 2.x"
    configureKtlintCli(format = false)
}

tasks.register<JavaExec>("ktlintFormat") {
    description = "Format Kotlin sources with ktlint 2.x"
    configureKtlintCli(format = true)
}

tasks.matching { it.name == "check" }.configureEach {
    dependsOn(ktlintCheck)
}

allprojects {
    pluginManager.withPlugin("dev.detekt") {
        extensions.configure<dev.detekt.gradle.extensions.DetektExtension> {
            toolVersion = detektVersion
            buildUponDefaultConfig = true
            allRules = false
            parallel = true
            config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            basePath.set(rootDir)
            ignoredBuildTypes = listOf("release")
        }
        tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
            jvmTarget.set("21")
            exclude("**/build/**")
            exclude("**/generated/**")
            reports {
                checkstyle.required.set(true)
                html.required.set(true)
                sarif.required.set(true)
            }
        }
        tasks.withType<dev.detekt.gradle.DetektCreateBaselineTask>().configureEach {
            jvmTarget.set("21")
            exclude("**/build/**")
            exclude("**/generated/**")
        }
    }
}

subprojects {
    apply(plugin = "dev.detekt")
}
