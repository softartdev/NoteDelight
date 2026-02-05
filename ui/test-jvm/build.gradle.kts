plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get().toInt())) }
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
}

dependencies {
    implementation(projects.ui.test)
    implementation(projects.core.domain)
    implementation(project(project.property("CORE_DATA_DB_MODULE").toString()))
    implementation(projects.core.presentation)
    implementation(projects.core.test)
    implementation(projects.ui.shared)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.testing)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.turbine)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.components.resources)
    implementation(libs.compose.ui.test.junit4)
    implementation(compose.desktop.currentOs)
    implementation(libs.material.theme.prefs)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kermit)
}
