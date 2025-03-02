plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.tomlkt)
}
