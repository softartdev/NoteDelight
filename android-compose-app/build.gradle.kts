import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.jetbrains.compose.ComposeBuildConfig.composeVersion

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
        versionCode = 80
        versionName = "8.0"
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
        kotlinCompilerExtensionVersion = composeVersion
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
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation(libs.decompose)
    implementation(libs.koin.android)
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    debugImplementation(libs.leakCanary.android)
    debugImplementation(libs.leakCanary.android.process)
    implementation(libs.leakCanary.plumber.android)
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.6")
    testImplementation(libs.junit)
    androidTestImplementation(project(":shared-android-test-util"))
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.leakCanary.android.instrumentation)
    androidTestUtil("androidx.test:orchestrator:1.4.1")
}
