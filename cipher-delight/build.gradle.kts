plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}
group = "com.softartdev"
version = "0.1"

kotlin {
    ios()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
    cocoapods {
        frameworkName = "CipherDelight"
        summary = "Cipher library for the apps with SQLDelight"
        homepage = "https://github.com/softartdev/NoteDelight"
        ios.deploymentTarget = "14.0"
//        pod("SQLCipher", "~> 4.4.2")
        useLibraries()
    }
}