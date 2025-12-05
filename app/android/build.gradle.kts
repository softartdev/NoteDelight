@file:Suppress("UnstableApiUsage")

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import com.google.firebase.crashlytics.buildtools.gradle.tasks.UploadMappingFileTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.gms)
    alias(libs.plugins.crashlytics)
}
apply(from = "$rootDir/gradle/common-android-sign-conf.gradle")

kotlin.compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())

android {
    namespace = "com.softartdev.notedelight"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.softartdev.noteroom"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 847
        versionName = "8.4.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        vectorDrawables.useSupportLibrary = true
        androidResources.localeFilters += setOf("en", "ru")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            configure<CrashlyticsExtension> { mappingFileUploadEnabled = false }
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            configure<CrashlyticsExtension> { mappingFileUploadEnabled = true }
            signingConfig = signingConfigs.getByName("config")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    }
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packagingOptions.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    testOptions{
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        emulatorControl.enable = true
    }
    androidResources.generateLocaleConfig = true
}

dependencies {
    implementation(projects.core.domain)
    implementation(project(project.property("CORE_DATA_DB_MODULE").toString()))
    implementation(projects.core.presentation)
    implementation(projects.ui.shared)
    implementation(kotlin("reflect"))
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(compose.ui)
    implementation(compose.material3)
    implementation(compose.preview)
    debugImplementation(compose.uiTooling)
    debugImplementation(libs.androidx.compose.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material.theme.prefs)
    implementation(libs.kermit)
    implementation(libs.kermit.crashlytics)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.tracing)
    debugImplementation(libs.leakCanary.android)
    debugImplementation(libs.leakCanary.android.process)
    implementation(libs.leakCanary.plumber.android)
    coreLibraryDesugaring(libs.desugar)
    testImplementation(libs.junit)
    testImplementation(libs.bundles.mockito)
    androidTestImplementation(projects.ui.testJvm) {
        exclude(group = "org.jetbrains.runtime", module = "jbr-api")
    }
    androidTestImplementation(libs.commonsware.saferoom)
    androidTestImplementation(libs.sqlDelight.android)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestUtil(libs.androidx.test.orchestrator)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.device)
    androidTestImplementation(compose.desktop.uiTestJUnit4)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.leakCanary.android.instrumentation)
    lintChecks(libs.android.security.lint)
}
tasks.withType<UploadMappingFileTask>{
    dependsOn("processDebugGoogleServices")
}
