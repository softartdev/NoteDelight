buildscript {
    val kotlin_version by extra("1.6.10")
    val coroutines_version by extra("1.6.1-native-mt")
    val sqldelight_version by extra("1.5.3")
    val decompose_version by extra("0.6.0")
    val koin_version by extra("3.2.0-beta-1")
    val napier_version by extra("2.5.0")
    val moko_resources_version by extra ("0.19.0")
    val firebase_version by extra ("29.3.1")
    val leak_canary_version by extra ("2.8.1")
    val junit_version by extra("4.13.2")
    val mockito_version by extra("4.4.0")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.squareup.sqldelight:gradle-plugin:$sqldelight_version")
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.5")
        classpath("com.slack.keeper:keeper:0.12.0")
        classpath ("dev.icerock.moko:resources-generator:$moko_resources_version")
        classpath ("org.jetbrains.compose:compose-gradle-plugin:1.1.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://s3.amazonaws.com/repo.commonsware.com")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
