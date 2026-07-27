@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm {
        compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
    }
    android {
        namespace = "com.softartdev.notedelight.ui.test"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
        }
        androidResources {
            enable = true
        }
        withHostTest { }
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = false
            freeCompilerArgs += "-Xoverride-konan-properties=minVersion.ios=14.1"
        }
    }
    wasmJs {
        browser()
    }
    swiftPMDependencies {
        iosMinimumDeploymentTarget = "14.1"
    }
    sourceSets {
        applyDefaultHierarchyTemplate()
        commonMain.dependencies {
            implementation(kotlin("test"))
            implementation(projects.core.domain)
            implementation(projects.core.presentation)
            implementation(projects.core.ui)
            implementation(projects.feature.backup.ui)
            api(projects.feature.biometric.domain)
            implementation(projects.feature.console.presentation)
            implementation(projects.feature.console.ui)
            implementation(libs.compose.ui.test)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.components.resources)
            implementation(libs.material.theme.prefs)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.runtime.testing)
            implementation(libs.turbine)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
        }
        androidMain.dependencies {
            implementation(libs.androidx.paging.common)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.test.ext.junit)
            implementation(libs.kotlinx.datetime)
        }
        val androidDeviceTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.androidx.test.runner)
                implementation(libs.androidx.compose.test.manifest)
                implementation(libs.espresso.core)
            }
        }
        all { languageSettings.optIn("kotlin.js.ExperimentalWasmJsInterop") }
    }
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
