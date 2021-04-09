plugins {
    kotlin("multiplatform")
}
group = "com.softartdev"
version = "0.1"

kotlin {
    jvm()
    ios()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api("com.squareup.okio:okio-multiplatform:2.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val iosMain by getting {
            dependencies {
                api("io.github.softartdev:sqlcipher-ktn-pod:0.6")
            }
        }
    }
}