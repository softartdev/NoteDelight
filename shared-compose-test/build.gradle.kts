import com.softartdev.notedelight.iosIntermediateSourceSets
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
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jdk.get()
        }
    }
    iosIntermediateSourceSets(iosArm64(), iosSimulatorArm64())
    applyDefaultHierarchyTemplate()

    cocoapods {
        name = "iosComposeTestPod"
        summary = "Tests for common UI-kit of the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        pod("SQLCipher", libs.versions.iosSqlCipher.get())
        framework {
            baseName = "iosComposeTestKit"
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
            implementation(project(":shared-compose-ui"))

            implementation(kotlin("test"))

            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)

            api(libs.mokoResources)
            api(libs.koin.core)
            implementation(libs.turbine)
        }
        commonTest.dependencies {
        }
        jvmMain.dependencies {
            implementation(libs.decompose)
            implementation(libs.koin.core.jvm)
            implementation(compose.desktop.uiTestJUnit4)
            implementation(compose.desktop.currentOs)
            implementation(libs.coroutines.swing)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}
