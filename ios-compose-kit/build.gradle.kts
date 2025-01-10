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
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        pod("SQLCipher", libs.versions.iosSqlCipher.get(), linkOnly = true)
        framework {
            baseName = "iosComposeKit"
            isStatic = true
            export(project(":core:domain"))
            export(project.dependencies.platform(libs.koin.bom))
            export(libs.koin.core)
        }
        if (!OperatingSystem.current().isMacOsX) noPodspec()
    }
    sourceSets {
        commonMain.dependencies {
            api(project(":core:domain"))
            api(project(":shared"))
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.runtime)
            implementation(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
        }
    }
}
