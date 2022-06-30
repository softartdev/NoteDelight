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
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.annotation:annotation:1.3.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")
    implementation("androidx.activity:activity-ktx:1.5.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("androidx.multidex:multidex:2.0.1")
    val archVersion = "2.1.0"
    implementation("androidx.arch.core:core-common:$archVersion")
    implementation("androidx.arch.core:core-runtime:$archVersion")
    implementation("com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.6")
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    debugImplementation(libs.leakCanary.android)
    debugImplementation(libs.leakCanary.android.process)
    implementation(libs.leakCanary.plumber.android)
    implementation(libs.koin.android)
    compileOnly("org.glassfish:javax.annotation:10.0-b28")
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation("com.google.android.gms:play-services-oss-licenses:17.0.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    testImplementation(project(":shared-android-test-util"))
    testImplementation(libs.junit)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation("androidx.arch.core:core-testing:$archVersion")
    testImplementation(libs.coroutines.test)
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.turbine)
    androidTestImplementation(project(":shared-android-test-util"))
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation(libs.coroutines.test)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    androidTestImplementation("androidx.arch.core:core-testing:$archVersion")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.leakCanary.android.instrumentation)
    androidTestUtil("androidx.test:orchestrator:1.4.1")
}
