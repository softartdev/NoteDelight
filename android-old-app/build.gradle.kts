import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.slack.keeper")
}
apply(from = "$rootDir/gradle/common-android-sign-conf.gradle")

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.softartdev.notedelight.old"
        minSdk = libs.versions.oldMinSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 75
        versionName = "7.5"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        vectorDrawables.useSupportLibrary = true
        resourceConfigurations += setOf("en", "ru")
    }
    buildFeatures {
        viewBinding = true
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
            signingConfig = signingConfigs.getByName("config")
        }
    }
    packagingOptions.resources.pickFirsts += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xuse-experimental=kotlin.Experimental",
            "-Xopt-in=kotlin.time.ExperimentalTime"
        )
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
    keeper {
//        enableL8RuleSharing.set(true)
        enableAssertions.set(false)
    }
//    testBuildType = "release"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":shared-jvm-util"))
    implementation(project(":shared-android-util"))
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.legacy.support)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.bundles.androidx.arch.core)
    implementation(libs.google.android.material)
    implementation(libs.google.android.gms.oss)
    implementation(libs.viewbinding.delegate)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    debugImplementation(libs.leakCanary.android)
    debugImplementation(libs.leakCanary.android.process)
    implementation(libs.leakCanary.plumber.android)
    implementation(libs.koin.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    coreLibraryDesugaring(libs.desugar)
    testImplementation(project(":shared-android-test-util"))
    testImplementation(libs.junit)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.turbine)
    androidTestImplementation(project(":shared-android-test-util"))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.bundles.androidx.test)
    androidTestUtil(libs.androidx.test.orchestrator)
    androidTestImplementation(libs.coroutines.test)
    androidTestImplementation(libs.bundles.espresso)
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.leakCanary.android.instrumentation)
}
