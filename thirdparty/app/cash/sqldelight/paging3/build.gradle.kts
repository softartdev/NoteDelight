@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
  jvmToolchain(libs.versions.jdk.get().toInt())
  jvm {
    compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
  }
  android {
    namespace = "sqldelight.androidx.paging3"
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
    commonMain.dependencies {
      implementation(libs.sqlDelight.runtime)
      implementation(libs.sqlDelight.asyncExt)
      implementation(libs.androidx.paging.common)
    }
  }
}
