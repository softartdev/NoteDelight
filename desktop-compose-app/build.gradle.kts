import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
}
group = "com.softartdev"
version = "1.0"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        jvmMain.dependencies {
            implementation(project(":shared"))
            implementation(project(":shared-compose-ui"))
            implementation(libs.decompose)
            implementation(libs.decompose.extComposeJb)
            implementation(libs.coroutines.swing)
            implementation(compose.desktop.currentOs)
            implementation(libs.koin.core.jvm)
        }
        jvmTest.dependencies {
            implementation(project(":jvm-compose-test"))
            implementation(kotlin("test"))
            implementation(compose.desktop.uiTestJUnit4)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompiler.get())
    desktop {
        application {
            mainClass = "com.softartdev.notedelight.MainKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "com.softartdev.notedelight"
                packageVersion = "1.0.1"
            }
        }
    }
}
