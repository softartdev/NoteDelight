buildscript {
    val kotlin_version by extra("1.4.30")
    val coroutines_version by extra("1.4.2-native-mt")
    val sqldelight_version by extra("1.4.4")
    val lifecycle_version by extra("2.2.0")
    val timber_version by extra("4.7.1")
    val junit_version by extra("4.13.2")
    val mockito_version by extra("3.8.0")
    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.squareup.sqldelight:gradle-plugin:$sqldelight_version")
        classpath("com.android.tools.build:gradle:7.0.0-alpha07")
        classpath("com.google.gms:google-services:4.3.5")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.0")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.2")
        classpath("com.slack.keeper:keeper:0.8.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://s3.amazonaws.com/repo.commonsware.com")
        maven(url = "https://kotlin.bintray.com/kotlinx/")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
