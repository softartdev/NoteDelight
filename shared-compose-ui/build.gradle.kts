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
                implementation("com.arkivanov.decompose:decompose:${rootProject.extra["decompose_version"]}")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:${rootProject.extra["decompose_version"]}")
                api("dev.icerock.moko:resources-compose:${rootProject.extra["moko_resources_version"]}")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.insert-koin:koin-androidx-compose:${rootProject.extra["koin_version"]}")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.insert-koin:koin-core-jvm:${rootProject.extra["koin_version"]}")
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