import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}
apply(from = "$rootDir/gradle/common-desktop-mac-sign-conf.gradle")
group = "com.softartdev"

kotlin {
    jvm {
        compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
    }
    applyDefaultHierarchyTemplate()

    sourceSets {
        jvmMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.presentation)
            implementation(projects.ui.shared)
            implementation(libs.androidx.navigation.compose)
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.swing)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.compose.material.icons.extended)
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.components.resources)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.kermit)
        }
        jvmTest.dependencies {
            implementation(projects.ui.test)
            implementation(projects.ui.testJvm)
            implementation(projects.feature.backup.ui)
            implementation(kotlin("test"))
            implementation(libs.compose.ui.test.junit4)
            implementation(compose.desktop.currentOs)
            implementation(libs.koin.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.lifecycle.runtime.testing)
        }
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.softartdev.notedelight.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Note Delight"
            packageVersion = "8.5.2"
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
