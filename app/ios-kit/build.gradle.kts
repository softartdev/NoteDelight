plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}
kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "iosComposeKit"
            isStatic = false
            freeCompilerArgs += listOf(
                "-Xoverride-konan-properties=minVersion.ios=15.0",
                "-linker-options",
                "-U _FIRCLSExceptionRecordNSException -U _OBJC_CLASS_\$_FIRStackFrame " +
                    "-U _OBJC_CLASS_\$_FIRExceptionModel -U _OBJC_CLASS_\$_FIRCrashlytics",
            )
            export(projects.core.domain)
            export(project.dependencies.platform(libs.koin.bom))
            export(libs.koin.core)
        }
    }
    applyDefaultHierarchyTemplate()

    swiftPMDependencies {
        iosMinimumDeploymentTarget = "15.0"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            api(projects.core.ui)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.kermit.crashlytics)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(projects.core.test.ui)
            implementation(libs.compose.ui.test)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.runtime.testing)
        }
    }
}
