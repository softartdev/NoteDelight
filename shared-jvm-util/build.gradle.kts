plugins {
    id("java-library")
    kotlin("jvm")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.kotlinx.datetime)
}