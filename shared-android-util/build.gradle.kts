import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 31
    defaultConfig {
        minSdk = 16
        targetSdk = 31
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlin.get()}")
    implementation(libs.koin.core.jvm)
    implementation(libs.napier)
    implementation(platform("com.google.firebase:firebase-bom:${rootProject.extra["firebase_version"]}"))
    implementation("com.google.firebase:firebase-crashlytics")
    testImplementation("junit:junit:${rootProject.extra["junit_version"]}")
    testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")
    testImplementation("org.mockito:mockito-inline:${rootProject.extra["mockito_version"]}")
    testImplementation("com.squareup.leakcanary:leakcanary-android-process:${rootProject.extra["leak_canary_version"]}")
}