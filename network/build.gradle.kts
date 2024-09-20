plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // serialization
    implementation(libs.kotlinx.serialization.json)

    // koin
    implementation(libs.koin.core)

}