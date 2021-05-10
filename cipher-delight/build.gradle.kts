plugins {
    kotlin("multiplatform")
}
group = "com.softartdev"
version = "0.1"

fun configInterop(target: org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget) {
    val main by target.compilations.getting
    val sqlite3 by main.cinterops.creating {
        includeDirs("$projectDir/src/include")
    }
}

kotlin {
    jvm()
    val knTarget = iosX64("ios")

    configInterop(knTarget)

    knTarget.let { target ->
        val test by target.compilations.getting
        test.kotlinOptions.freeCompilerArgs += listOf("-linker-options", "-lsqlite3")
    }
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
//                api("io.github.softartdev:sqlcipher-ktn-pod:1.0")
//                implementation("co.touchlab:sqliter:1.0.0")
            }
        }
    }
}