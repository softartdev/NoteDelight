@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm()
    android {
        namespace = "com.softartdev.notedelight.feature.biometric.domain"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
        }
    }
    iosArm64()
    iosSimulatorArm64()
    wasmJs {
        browser()
    }
    sourceSets.forEach {
        it.dependencies {
            implementation(project.dependencies.enforcedPlatform(libs.coroutines.bom))
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.biometric)
        }
    }
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}
