@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm()
    androidTarget()
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
            implementation(project(":core:domain"))
            implementation(libs.coroutines.core)
            implementation(libs.napier)
            implementation(libs.okio)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.okio.fakefilesystem)
            implementation(libs.coroutines.test)
        }
        androidMain.dependencies {
        }
        androidUnitTest.dependencies {
        }
        iosMain.dependencies {
        }
        iosTest.dependencies {
        }
        jvmMain.dependencies {
            implementation(libs.appdirs)
        }
        jvmTest.dependencies {
        }
        wasmJsMain.dependencies {
        }
        wasmJsTest.dependencies {
        }
    }
}
android {
    namespace = "com.softartdev.notedelight.core.data.fileexplorer"
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    }
    dependencies {
        coreLibraryDesugaring(libs.desugar)
    }
    testOptions.unitTests.isReturnDefaultValues = true
}

