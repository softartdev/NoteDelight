plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.parcelize)
}
compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompiler.get())
}
kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jdk.get()
        }
    }
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.decompose)
            implementation(libs.decompose.extComposeJb)
            implementation(libs.koin.core)
            api(libs.mokoResources.compose)
        }
        androidMain.dependencies {
            implementation(libs.koin.androidx.compose)
        }
        jvmMain.dependencies {
            implementation(libs.koin.core.jvm)
        }
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(compose.desktop.uiTestJUnit4)
        }
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

android {
    namespace = "com.softartdev.notedelight.shared.compose"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig.minSdk = libs.versions.minSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    }
}
