@file:OptIn(ExperimentalWasmDsl::class)

import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.cocoapods)
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
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    wasmJs {
        browser()
        binaries.executable()
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(project.property("CORE_DATA_DB_MODULE").toString()))
            implementation(project(":core:data:file-explorer"))
            implementation(project(":core:presentation"))
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.adaptive)
            implementation(libs.compose.adaptive.layout)
            implementation(libs.compose.adaptive.navigation)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.material.theme.prefs)
            implementation(project(":thirdparty:androidx:paging:compose"))
            implementation(libs.kotlinx.datetime)
            implementation(libs.napier)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.androidx.compose)
            implementation(compose.preview)
            implementation(libs.androidx.ui.tooling)
            implementation(libs.accompanist.permissions)
        }
        jvmMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        jvmTest.dependencies {
            implementation(project(":core:test"))
            implementation(kotlin("test"))
            implementation(compose.desktop.uiTestJUnit4)
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
    cocoapods {
        summary = "Shared library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        pod("SQLCipher", libs.versions.iosSqlCipher.get(), linkOnly = true)
        framework {
            isStatic = false
        }
        if (!OperatingSystem.current().isMacOsX) noPodspec()
    }
}

android {
    namespace = "com.softartdev.notedelight.shared.compose"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig.minSdk = libs.versions.minSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    }
}
