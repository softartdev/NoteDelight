@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}
compose.resources {
    publicResClass = true
    generateResClass = always
}
kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm {
        compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
    }
    android {
        namespace = "com.softartdev.notedelight.shared.compose"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
        }
        androidResources {
            enable = true
        }
        withHostTest { }
    }
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = false
            freeCompilerArgs += "-Xoverride-konan-properties=minVersion.ios=15.0"
        }
    }
    wasmJs {
        browser()
        binaries.executable()
    }
    swiftPMDependencies {
        iosMinimumDeploymentTarget = "15.0"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(project(project.property("CORE_DATA_DB_MODULE").toString()))
            implementation(projects.core.presentation)
            implementation(projects.feature.backup.domain)
            implementation(projects.feature.backup.ui)
            implementation(projects.feature.biometric.domain)
            implementation(projects.feature.biometric.presentation)
            implementation(projects.feature.fileExplorer.data)
            implementation(projects.feature.console.domain)
            implementation(projects.feature.console.presentation)
            implementation(projects.feature.console.ui)
            implementation(libs.compose.ui)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.adaptive)
            implementation(libs.compose.adaptive.layout)
            implementation(libs.compose.adaptive.navigation)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.navigationevent.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.material.theme.prefs)
            implementation(libs.androidx.paging.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kermit)
            implementation(libs.kermit.koin)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.androidx.compose)
            implementation(libs.compose.ui.tooling)
            implementation(libs.androidx.ui.tooling)
            implementation(libs.accompanist.permissions)
        }
        jvmMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        jvmTest.dependencies {
            implementation(projects.core.test.common)
            implementation(kotlin("test"))
            implementation(libs.compose.ui.test.junit4)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.test)
        }
        iosMain.dependencies {
        }
        wasmJsMain.dependencies {
        }
        wasmJsTest.dependencies {
        }
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
