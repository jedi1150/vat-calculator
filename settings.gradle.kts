pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
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
