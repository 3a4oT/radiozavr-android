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

rootProject.name = "radio-lux-fm-streamer"

// Main app
include(":app")

// Admin app
include(":admin")

// Core modules
include(":core:models")
include(":core:ui-theme")
include(":core:ui-components")
include(":core:network")
include(":core:config")
