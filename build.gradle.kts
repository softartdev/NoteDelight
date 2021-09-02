buildscript {
    val kotlin_version by extra("1.5.30")
    val coroutines_version by extra("1.5.1-native-mt")
    val sqldelight_version by extra("1.5.1")
    val timber_version by extra("5.0.1")
    val junit_version by extra("4.13.2")
    val mockito_version by extra("3.12.4")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.squareup.sqldelight:gradle-plugin:$sqldelight_version")
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.4")
        classpath("com.slack.keeper:keeper:0.11.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://s3.amazonaws.com/repo.commonsware.com")
        maven(url = "https://kotlin.bintray.com/kotlinx/")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
