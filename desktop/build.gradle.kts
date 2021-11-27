import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}
group = "com.softartdev"
version = "1.0"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(project(":shared-compose-ui"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${rootProject.extra["coroutines_version"]}")
                implementation(compose.desktop.currentOs)
                implementation("io.insert-koin:koin-core-jvm:${rootProject.extra["koin_version"]}")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.softartdev.notedelight"
            packageVersion = "1.0.0"
        }
    }
}