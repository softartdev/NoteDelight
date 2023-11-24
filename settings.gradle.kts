rootProject.name = "NoteDelight"

include(":shared")
include(":shared-compose-ui")
include(":jvm-compose-test")
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
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
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