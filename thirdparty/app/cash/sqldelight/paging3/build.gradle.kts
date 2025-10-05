@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
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
  }
  sourceSets {
    commonMain.dependencies {
      implementation(libs.sqlDelight.runtime)
      implementation(libs.sqlDelight.asyncExt)
      implementation(libs.androidx.paging.common)
    }
  }
}

android {
  namespace = "sqldelight.androidx.paging3"
  compileSdk = libs.versions.compileSdk.get().toInt()
  defaultConfig.minSdk = libs.versions.minSdk.get().toInt()
  compileOptions {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
  }
}
