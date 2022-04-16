import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("kotlin-parcelize")
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    android()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                api("io.github.softartdev:material-theme-prefs:0.2")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.desktop.common)
                implementation("org.jetbrains.compose.material:material:${org.jetbrains.compose.ComposeBuildConfig.composeVersion}")
                implementation("org.jetbrains.compose.material:material-icons-extended:${org.jetbrains.compose.ComposeBuildConfig.composeVersion}")
                implementation(libs.decompose)
                implementation(libs.decompose.extComposeJb)
                api(libs.mokoResources.compose)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.androidx.compose)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.koin.core.jvm)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(compose("org.jetbrains.compose.ui:ui-test-junit4"))
            }
        }
    }
}
android {
    compileSdk = 31
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}