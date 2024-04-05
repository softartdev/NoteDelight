import com.softartdev.notedelight.iosIntermediateSourceSets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
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

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))
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

            implementation(libs.koin.core)
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
