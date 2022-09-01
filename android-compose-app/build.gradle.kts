@file:OptIn(ExperimentalComposeLibrary::class)

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.slack.keeper")
}
apply(from = "$rootDir/gradle/common-android-sign-conf.gradle")

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.softartdev.noteroom"
        minSdk = libs.versions.composeMinSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 82
        versionName = "8.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        vectorDrawables.useSupportLibrary = true
        resourceConfigurations += setOf("en", "ru")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            configure<CrashlyticsExtension> { mappingFileUploadEnabled = false }
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            configure<CrashlyticsExtension> { mappingFileUploadEnabled = true }
            signingConfig = signingConfigs.getByName("config")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"//FIXME org.jetbrains.compose.ComposeBuildConfig.composeVersion
    }
    packagingOptions.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
//    testBuildType = "release"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":shared-compose-ui"))
    implementation(project(":shared-android-util"))
    implementation(libs.androidx.activity.compose)
    implementation(compose.ui)
    implementation(compose.material)
    implementation(compose.preview)
    debugImplementation(compose.uiTooling)
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.2.1")//FIXME compose.uiTestManifest
    implementation(libs.decompose)
    implementation(libs.koin.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    debugImplementation(libs.leakCanary.android)
    debugImplementation(libs.leakCanary.android.process)
    implementation(libs.leakCanary.plumber.android)
    coreLibraryDesugaring(libs.desugar)
    testImplementation(libs.junit)
    androidTestImplementation(project(":shared-android-test-util"))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestUtil(libs.androidx.test.orchestrator)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(compose.uiTestJUnit4)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.leakCanary.android.instrumentation)
}
