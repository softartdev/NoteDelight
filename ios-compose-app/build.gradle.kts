import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("dev.icerock.mobile.multiplatform-resources")
}

val binConfig: KotlinNativeTarget.() -> Unit = {
    binaries {
        executable {
            entryPoint = "com.softartdev.notedelight.main"
            freeCompilerArgs += listOf(
                "-linker-option", "-framework", "-linker-option", "Metal",
                "-linker-option", "-framework", "-linker-option", "CoreText",
                "-linker-option", "-framework", "-linker-option", "CoreGraphics"
            )
            linkerOpts.add("-lsqlite3")
        }
    }
}

kotlin {
    iosX64("uikitX64", binConfig)
    iosArm64("uikitArm64", binConfig)
//    iosSimulatorArm64("uikitSimulatorArm64", binConfig)
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(project(":shared-compose-ui"))
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(libs.mokoResources)
                implementation(libs.koin.core)
            }
        }
        val uikitX64Main by getting
        val uikitArm64Main by getting
//        val uikitSimulatorArm64Main by getting
        val uikitMain by creating {
            dependsOn(commonMain)
            uikitX64Main.dependsOn(this)
            uikitArm64Main.dependsOn(this)
//            uikitSimulatorArm64Main.dependsOn(this)
            dependencies {
            }
        }
    }
}

kotlin.targets.withType<KotlinNativeTarget> {
    binaries.all {
        binaryOptions["memoryModel"] = "experimental"
        binaryOptions["freezing"] = "disabled"
    }
}

kotlin {
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.sofartdev.notedelight"
}
