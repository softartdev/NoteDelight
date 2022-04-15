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
    compileSdk = 31
    defaultConfig {
        applicationId = "com.softartdev.noteroom"
        minSdk = 21
        targetSdk = 31
        versionCode = 77
        versionName = "7.7"
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
    implementation("com.arkivanov.decompose:decompose:${rootProject.extra["decompose_version"]}")
    implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation(platform("com.google.firebase:firebase-bom:${rootProject.extra["firebase_version"]}"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:${rootProject.extra["leak_canary_version"]}")
    debugImplementation("com.squareup.leakcanary:leakcanary-android-process:${rootProject.extra["leak_canary_version"]}")
    implementation("com.squareup.leakcanary:plumber-android:${rootProject.extra["leak_canary_version"]}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    testImplementation("junit:junit:${rootProject.extra["junit_version"]}")
    androidTestImplementation(project(":shared-android-test-util"))
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("com.squareup.leakcanary:leakcanary-android-instrumentation:${rootProject.extra["leak_canary_version"]}")
    androidTestUtil("androidx.test:orchestrator:1.4.1")
}
