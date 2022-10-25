pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}
rootProject.name = "NoteDelight"

include(":shared")
include(":shared-compose-ui")
include(":shared-jvm-util")
include(":shared-android-util")
include(":shared-android-test-util")
//include(":android-old-app")
include(":android-compose-app")
include(":desktop-compose-app")
include(":ios-compose-app")
