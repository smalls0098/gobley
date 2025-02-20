plugins {
    id("uniffi-tests")
}

uniffi {
    generateFromLibrary {
        namespace = "imported_types_lib"
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.http)
                api(project(":examples:custom-types"))
                api(project(":tests:uniffi:ext-types:custom-types"))
                api(project(":tests:uniffi:ext-types:uniffi-one"))
                api(project(":tests:uniffi:ext-types:sub-lib"))
            }
        }
    }
}