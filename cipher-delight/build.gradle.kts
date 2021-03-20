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
}