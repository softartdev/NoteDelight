buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
        classpath(libs.sqlDelight.gradlePlugin)
        classpath("com.android.tools.build:gradle:7.4.1")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.3")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
        classpath(libs.mokoResources.generator)
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.3.0")
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
