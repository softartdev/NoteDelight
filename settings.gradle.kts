rootProject.name = "NoteDelight"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":core:domain")
//include(":core:data:db-room")
include(":core:data:db-sqldelight")
include(":core:data:file-explorer")
include(":core:presentation")
include(":core:test")
include(":ui:shared")
include(":ui:test-jvm")
include(":thirdparty:androidx:paging:compose")
include(":thirdparty:app:cash:sqldelight:paging3")
include(":app:android")
include(":app:desktop")
include(":app:ios-kit")
include(":app:web")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://s3.amazonaws.com/repo.commonsware.com")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}