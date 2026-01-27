import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.cocoapods)
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm()
    android {
        namespace = "com.softartdev.notedelight.core.data"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
        }
        withHostTest { }
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(libs.room.runtime)
            implementation(libs.room.paging)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.datetime)
            implementation(libs.cashapp.paging.common)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(projects.core.test)
            implementation(libs.room.testing)
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.test)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.commonsware.saferoom)
            implementation(libs.android.sqlcipher)
        }
        val androidHostTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.bundles.mockito)
            }
        }
        iosMain.dependencies {
        }
        iosTest.dependencies {
        }
        jvmMain.dependencies {
            implementation(libs.appdirs)
            implementation(libs.kermit)
        }
        jvmTest.dependencies {
        }
    }
    cocoapods {
        summary = "Data library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        pod("SQLCipher", libs.versions.iosSqlCipher.get())
        framework {
            isStatic = true
        }
        if (!OperatingSystem.current().isMacOsX) noPodspec()
    }
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
//    ksp(libs.room.compiler)
//    kspCommonMainMetadata(libs.room.compiler)
    add("kspAndroid", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    coreLibraryDesugaring(libs.desugar)
}
