import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}
group = "com.softartdev"

kotlin {
    jvm {
        compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
    }
    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        jvmMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:presentation"))
            implementation(project(":shared"))
            implementation(libs.androidx.navigation.compose)
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.swing)
            implementation(compose.desktop.currentOs)
            implementation(compose.components.resources)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.napier)
        }
        jvmTest.dependencies {
            implementation(project(":jvm-compose-test"))
            implementation(kotlin("test"))
            implementation(compose.desktop.uiTestJUnit4)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.softartdev.notedelight.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Note Delight"
            packageVersion = "1.1.6"
            description = "Note app with encryption"
            copyright = "© 2023 SoftArtDev"
            macOS.iconFile.set(project.file("src/jvmMain/resources/app_icon.icns"))
            windows.iconFile.set(project.file("src/jvmMain/resources/app_icon.ico"))
            linux.iconFile.set(project.file("src/jvmMain/resources/app_icon.png"))
            modules("java.sql")
        }
        buildTypes.release.proguard {
            isEnabled = false //FIXME
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}
