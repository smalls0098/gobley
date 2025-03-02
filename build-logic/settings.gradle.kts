pluginManagement {
    includeBuild("gobley-gradle-build")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(":gobley-gradle")
include(":gobley-gradle-rust")
include(":gobley-gradle-cargo")
include(":gobley-gradle-uniffi")
include(":conventions")
