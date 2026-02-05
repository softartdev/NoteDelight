import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}
kotlin {
    iosArm64()
    iosSimulatorArm64()
    applyDefaultHierarchyTemplate()

    cocoapods {
        name = "iosComposePod"
        summary = "Common UI-kit for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        pod("SQLCipher", libs.versions.iosSqlCipher.get(), linkOnly = true)
        framework {
            baseName = "iosComposeKit"
            isStatic = false
            export(projects.core.domain)
            export(project.dependencies.platform(libs.koin.bom))
            export(libs.koin.core)
        }
        if (!OperatingSystem.current().isMacOsX) noPodspec()
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            api(projects.ui.shared)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(projects.ui.test)
            implementation(libs.compose.ui.test)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.runtime.testing)
        }
    }
}
