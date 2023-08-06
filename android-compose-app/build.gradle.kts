@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalComposeLibrary::class)

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.gms)
    alias(libs.plugins.crashlytics)
}
apply(from = "$rootDir/gradle/common-android-sign-conf.gradle")
compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompiler.get())
}
android {
    namespace = "com.softartdev.notedelight"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.softartdev.noteroom"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 831
        versionName = "8.3.1"
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
    buildFeatures.compose = true
    packagingOptions.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
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
    debugImplementation(libs.androidx.compose.test.manifest)
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
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestUtil(libs.androidx.test.orchestrator)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(compose.uiTestJUnit4)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.leakCanary.android.instrumentation)
}
