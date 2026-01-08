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
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.paging.common)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
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
        }
        jvmTest.dependencies {
        }
        wasmJsMain.dependencies {
        }
        wasmJsTest.dependencies {
        }
    }
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
android {
    namespace = "com.softartdev.notedelight.core.domain"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig.minSdk = libs.versions.minSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
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
