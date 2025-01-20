pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "uniffi-kotlin-multiplatform-bindings"

include(":tests:callbacks")
include(":tests:chronological")
include(":tests:coverall")
include(":tests:docstring")
include(":tests:docstring-proc-macro")
include(":tests:enum-types")
include(":tests:error-types")
// Temporarily disable ext-types test.
// TODO:
//   1. Properly handle external types in headers
//   2. Fix panics by uniffi-meta during bindings generation in ext-types
// include(":tests:ext-types:custom-types")
// include(":tests:ext-types:ext-types")
// include(":tests:ext-types:ext-types-proc-macro")
// include(":tests:ext-types:http-headermap")
// include(":tests:ext-types:sub-lib")
// include(":tests:ext-types:uniffi-one")
include(":tests:futures")
include(":tests:keywords")
include(":tests:proc-macro")
include(":tests:simple-fns")
include(":tests:simple-iface")
include(":tests:struct-default-values")
include(":tests:trait-methods")
include(":tests:type-limits")

include(":tests:gradle:android-linking")
include(":tests:gradle:cargo-only")
include(":tests:gradle:no-uniffi-block")

include(":examples:app")
include(":examples:arithmetic-procmacro")
include(":examples:audio-cpp-app")
include(":examples:custom-types")
include(":examples:todolist")
include(":examples:tokio-blake3-app")
