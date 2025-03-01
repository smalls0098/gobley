plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.buildconfig)
}

val bindgenInfo = BindgenInfo.fromCargoManifest(
    rootProject.layout.projectDirectory.file("../bindgen/Cargo.toml").asFile
)

group = "dev.gobley.uniffi"
description = "Gradle UniFFI Plugin"
version = bindgenInfo.version

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(gradleKotlinDsl())
    compileOnly(plugin(libs.plugins.kotlin.jvm))
    implementation(plugin(libs.plugins.android.application))
    implementation(plugin(libs.plugins.android.library))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.tomlkt)
    implementation(libs.jna)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
}

tasks.withType<Test> {
    useJUnitPlatform()
    reports {
        junitXml.required.set(true)
    }
}

buildConfig {
    packageName = "dev.gobley.uniffi.gradle"

    buildConfigField("String", "BINDGEN_VERSION", "\"${bindgenInfo.version}\"")
    buildConfigField("String", "BINDGEN_CRATE", "\"${bindgenInfo.name}\"")
    buildConfigField("String", "BINDGEN_BIN", "\"${bindgenInfo.binaryName}\"")

    forClass("DependencyVersions") {
        buildConfigField("String", "OKIO", "\"${libs.versions.okio.get()}\"")
        buildConfigField("String", "KOTLINX_ATOMICFU", "\"${libs.versions.kotlinx.atomicfu.get()}\"")
        buildConfigField("String", "KOTLINX_DATETIME", "\"${libs.versions.kotlinx.datetime.get()}\"")
        buildConfigField("String", "KOTLINX_COROUTINES", "\"${libs.versions.kotlinx.coroutines.get()}\"")
        buildConfigField("String", "JNA", "\"${libs.versions.jna.get()}\"")
        buildConfigField("String", "ANDROIDX_ANNOTATION", "\"${libs.versions.androidx.annotation.get()}\"")
    }

    forClass("PluginIds") {
        buildConfigField("String", "KOTLIN_MULTIPLATFORM", "\"${libs.plugins.kotlin.multiplatform.get().pluginId}\"")
        buildConfigField("String", "KOTLIN_ATOMIC_FU", "\"${libs.plugins.kotlin.atomicfu.get().pluginId}\"")
        buildConfigField("String", "KOTLIN_SERIALIZATION", "\"${libs.plugins.kotlin.serialization.get().pluginId}\"")
        buildConfigField("String", "ANDROID_APPLICATION", "\"${libs.plugins.android.application.get().pluginId}\"")
        buildConfigField("String", "ANDROID_LIBRARY", "\"${libs.plugins.android.library.get().pluginId}\"")
        buildConfigField("String", "RUST_KOTLIN_MULTIPLATFORM", "\"dev.gobley.rust\"")
        buildConfigField("String", "CARGO_KOTLIN_MULTIPLATFORM", "\"dev.gobley.cargo\"")
        buildConfigField("String", "UNIFFI_KOTLIN_MULTIPLATFORM", "\"dev.gobley.uniffi\"")
    }
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    vcsUrl.set("https://github.com/gobley/gobley")
    website.set(vcsUrl)

    plugins {
        create("gobley-gradle-cargo") {
            id = "dev.gobley.cargo"
            displayName = name
            implementationClass = "dev.gobley.gradle.cargo.CargoPlugin"
            description = "A plugin for building Rust libraries and link them to Kotlin projects."
            tags.addAll("rust", "kotlin", "kotlin-multiplatform")
        }
        create("gobley-gradle-uniffi") {
            id = "dev.gobley.uniffi"
            displayName = name
            implementationClass = "dev.gobley.gradle.uniffi.UniFfiPlugin"
            description = "A plugin for generating UniFFI Kotlin Multiplatform bindings for Rust libraries."
            tags.addAll("uniffi", "rust", "kotlin", "kotlin-multiplatform")
        }
        create("gobley-gradle-rust") {
            id = "dev.gobley.rust"
            displayName = name
            implementationClass = "dev.gobley.gradle.rust.RustPlugin"
            description = "A plugin for configuring Rust toolchain and linking Rust libraries to Kotlin projects."
            tags.addAll("rust", "kotlin", "kotlin-multiplatform")
        }
    }
}

tasks.publish {
    dependsOn(tasks.publishPlugins)
}
