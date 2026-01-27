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
    namespace = "androidx.paging.compose"
    compileSdk = libs.versions.compileSdk.get().toInt()
    minSdk = libs.versions.minSdk.get().toInt()
    compilerOptions {
      jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
    }
  }
  iosArm64()
  iosSimulatorArm64()
  applyDefaultHierarchyTemplate()
  wasmJs {
    browser()
  }
  sourceSets {
    commonMain.dependencies {
      api(libs.androidx.paging.common)
      api(compose.runtime)
    }
  }
}
