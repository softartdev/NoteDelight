import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.compose)
}
compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompiler.get())
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
        pod("SQLCipher", libs.versions.iosSqlCipher.get())
        framework {
            baseName = "iosComposeKit"
            isStatic = true
            export(project(":shared"))
            export(libs.mokoResources)
            export(libs.koin.core)
        }
        if (!OperatingSystem.current().isMacOsX) noPodspec()
    }
    sourceSets {
        commonMain.dependencies {
            api(project(":shared"))
            api(project(":shared-compose-ui"))
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.runtime)
            api(libs.mokoResources)
            api(libs.koin.core)
        }
    }
}
