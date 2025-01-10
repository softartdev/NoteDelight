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
            implementation(libs.kotlinx.datetime)
            implementation(libs.cashapp.paging.common)
            implementation(libs.napier)
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
    }
}
android {
    namespace = "com.softartdev.notedelight.core.domain"
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
