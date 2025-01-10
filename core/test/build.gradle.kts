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

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.test)
            implementation(libs.napier)
        }
        androidMain.dependencies {
            implementation(libs.bundles.mockito)
        }
        iosMain.dependencies {
        }
        jvmMain.dependencies {
        }
    }
}
android {
    namespace = "com.softartdev.notedelight.core.test"
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
