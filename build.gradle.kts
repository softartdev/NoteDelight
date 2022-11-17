buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
        classpath(libs.sqlDelight.gradlePlugin)
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("com.google.gms:google-services:4.3.13")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.1")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.5")
        classpath("com.slack.keeper:keeper:0.12.0")
        classpath(libs.mokoResources.generator)
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.3.0-beta01")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://s3.amazonaws.com/repo.commonsware.com")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
