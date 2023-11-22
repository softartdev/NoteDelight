plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
}

compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompiler.get())
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(11)) }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":shared-compose-ui"))
    implementation(libs.decompose)
    implementation(libs.koin.core.jvm)
    implementation(libs.turbine)
    implementation(compose.desktop.uiTestJUnit4)
    implementation(compose.desktop.currentOs)
}
