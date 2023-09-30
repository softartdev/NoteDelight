plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.compose)
}
compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompiler.get())
}
kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

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
            if (rootProject.extra["hasXcode15"] == true) linkerOpts += "-ld64" //TODO: remove after update Kotlin >= 1.9.10
            export(project(":shared"))
            export(libs.mokoResources)
            export(libs.koin.core)
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
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
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}
