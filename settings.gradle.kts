rootProject.name = "NoteDelight"

include(":core:domain")
include(":core:data:db-sqldelight")
//include(":core:data:db-room")
include(":core:presentation")
include(":core:test")
include(":jvm-compose-test")
include(":thirdparty:androidx:paging:compose")
include(":shared")
include(":android-compose-app")
include(":desktop-compose-app")
include(":ios-compose-kit")

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
    id("com.gradle.develocity") version "4.1"
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