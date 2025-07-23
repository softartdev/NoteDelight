import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  jvmToolchain(libs.versions.jdk.get().toInt())
  jvm {
    compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
  }
  androidTarget()
  iosArm64()
  iosSimulatorArm64()
  applyDefaultHierarchyTemplate()

  sourceSets {
    commonMain.dependencies {
      api(libs.androidx.paging.common)
      api(compose.runtime)
    }
  }
}

android {
  namespace = "androidx.paging.compose"
  compileSdk = libs.versions.compileSdk.get().toInt()
  defaultConfig.minSdk = libs.versions.minSdk.get().toInt()
  compileOptions {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
  }
}
