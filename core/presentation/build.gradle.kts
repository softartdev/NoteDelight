plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
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
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.napier)
            implementation(libs.cashapp.paging.common)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(project(":core:test"))
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.test)
            implementation(libs.turbine)
        }
        androidMain.dependencies {
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation(libs.bundles.mockito)
            implementation(libs.androidx.arch.core.testing)
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
    namespace = "com.softartdev.notedelight.core.presentation"
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
    lint.disable += setOf("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition") // FIXME remove after AGP update
    dependencies {
        coreLibraryDesugaring(libs.desugar)
    }
    testOptions.unitTests.isReturnDefaultValues = true
}
