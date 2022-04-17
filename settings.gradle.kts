pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
rootProject.name = "NoteDelight"

include(":shared")
include(":shared-compose-ui")
include(":shared-android-util")
include(":shared-android-test-util")
include(":android-old-app")
include(":android-compose-app")
include(":desktop")