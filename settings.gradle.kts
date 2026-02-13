pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "vatcalculator"
include(":app")
include(":feature:calculator")
include(":feature:settings")
include(":core:data")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:designsystem")
include(":core:model")
include(":lib")
