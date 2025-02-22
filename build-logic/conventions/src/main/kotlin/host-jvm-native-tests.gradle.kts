import io.gitlab.trixnity.gradle.RustHost
import io.gitlab.trixnity.gradle.rust.dsl.hostNativeTarget
import io.gitlab.trixnity.gradle.rust.dsl.useRustUpLinker
import org.gradle.accessors.dm.*

plugins {
    id("io.gitlab.trixnity.rust.kotlin.multiplatform")
    kotlin("multiplatform")
    kotlin("plugin.atomicfu")
}

// https://github.com/gradle/gradle/issues/15383
apply<VersionCatalogPlugin>()
val libs = extensions.getByName("libs") as LibrariesForLibs

kotlin {
    jvmToolchain(17)
    jvm()
    hostNativeTarget {
        if (RustHost.Platform.Windows.isCurrent) {
            compilations.getByName("test") {
                useRustUpLinker()
            }
        }
    }
    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotest.assertions.core)
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    reports {
        junitXml.required.set(true)
    }
}
