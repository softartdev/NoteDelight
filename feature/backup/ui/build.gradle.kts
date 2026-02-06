@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm {
        compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
    }
    android {
        namespace = "com.softartdev.notedelight.feature.backup.ui"
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
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.compose)
                implementation(libs.kermit)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
            }
        }
        val jvmMain by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val wasmJsMain by getting {
            dependencies {
                implementation(projects.feature.backup.domain)
                implementation(libs.coroutines.core)
            }
        }
    }
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
