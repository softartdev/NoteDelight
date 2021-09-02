import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.slack.keeper")
}
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(keystorePropertiesFile.inputStream())

android {
    signingConfigs {
        create("config") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.softartdev.noteroom"
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = 74
        versionName = "7.4"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        testInstrumentationRunnerArgument("listener", "leakcanary.FailTestOnLeakRunListener")
        vectorDrawables.useSupportLibrary = true
        resConfigs("en", "ru")
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false          // to disable mapping file uploads (default=true if minifying)
            }
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("config")
        }
    }
    packagingOptions {
        pickFirst("META-INF/AL2.0")
        pickFirst("META-INF/LGPL2.1")
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += listOf("-Xuse-experimental=kotlin.Experimental", "-Xopt-in=kotlin.time.ExperimentalTime")
    }
    lintOptions {
        isAbortOnError = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
    keeper {
//        enableL8RuleSharing.set(true)
        enableAssertions.set(false)
    }
    testBuildType = "release"
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.annotation:annotation:1.2.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha03")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.activity:activity-ktx:1.3.1")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.multidex:multidex:2.0.1")
    val archVersion = "2.1.0"
    implementation("androidx.arch.core:core-common:$archVersion")
    implementation("androidx.arch.core:core-runtime:$archVersion")
    implementation("com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.4.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines_version"]}")
    implementation("com.jakewharton.timber:timber:${rootProject.extra["timber_version"]}")
    val leakCanaryVersion = "2.7"
    debugImplementation("com.squareup.leakcanary:leakcanary-android-process:$leakCanaryVersion")
    implementation("com.squareup.leakcanary:plumber-android:$leakCanaryVersion")
    val koinVersion = "3.1.2"
    implementation("io.insert-koin:koin-android:$koinVersion")
    compileOnly("org.glassfish:javax.annotation:10.0-b28")
    implementation(platform("com.google.firebase:firebase-bom:28.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.android.gms:play-services-oss-licenses:17.0.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
    testImplementation(project(":shared-android-test-util"))
    testImplementation("junit:junit:${rootProject.extra["junit_version"]}")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.insert-koin:koin-test-junit4:$koinVersion")
    testImplementation("androidx.arch.core:core-testing:$archVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines_version"]}")
    testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")
    testImplementation("org.mockito:mockito-inline:${rootProject.extra["mockito_version"]}")
    testImplementation("app.cash.turbine:turbine:0.7.0-SNAPSHOT")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines_version"]}")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    androidTestImplementation("androidx.arch.core:core-testing:$archVersion")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("com.squareup.leakcanary:leakcanary-android-instrumentation:$leakCanaryVersion")
    androidTestUtil("androidx.test:orchestrator:1.4.0")
}