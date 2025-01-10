import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlin.cocoapods)
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm()
    androidTarget()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(libs.sqlDelight.runtime)
            implementation(libs.sqlDelight.coroutinesExt)
            implementation(libs.sqlDelight.paging)
            implementation(libs.kotlinx.datetime)
            implementation(libs.stately.common)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":core:test"))
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.test)
            implementation(libs.napier)
        }
        androidMain.dependencies {
            implementation(libs.sqlDelight.android)
            implementation(libs.commonsware.saferoom)
            implementation(libs.android.sqlcipher)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation(libs.bundles.mockito)
            implementation(libs.sqlDelight.jvm)
        }
        iosMain.dependencies {
            implementation(libs.sqlDelight.native)
            implementation(libs.stately.isolate)
            implementation(libs.stately.iso.collections)
        }
        iosTest.dependencies {
        }
        jvmMain.dependencies {
            implementation(libs.sqlDelight.jvm)
            implementation(libs.appdirs)
            implementation(libs.napier)
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
}

android {
    namespace = "com.softartdev.notedelight.core.data"
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    }
    dependencies {
        coreLibraryDesugaring(libs.desugar)
    }
    testOptions.unitTests.isReturnDefaultValues = true
}

sqldelight {
    databases {
        create("NoteDb") {
            packageName.set("com.softartdev.notedelight.db")
        }
    }
    linkSqlite.set(false)
}
