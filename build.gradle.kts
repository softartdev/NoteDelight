buildscript {
    val firebase_version by extra("29.3.1")
    val leak_canary_version by extra("2.8.1")
    val junit_version by extra("4.13.2")
    val mockito_version by extra("4.4.0")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
        classpath(libs.sqlDelight.gradlePlugin)
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.5")
        classpath("com.slack.keeper:keeper:0.12.0")
        classpath(libs.mokoResources.generator)
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.1.1")
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
