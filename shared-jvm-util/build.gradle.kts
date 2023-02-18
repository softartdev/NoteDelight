plugins {
    id("java-library")
    kotlin("jvm")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(11)) }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.kotlinx.datetime)
}