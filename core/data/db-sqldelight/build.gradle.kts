@file:OptIn(ExperimentalWasmDsl::class)

import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlin.cocoapods)
}

// Replace standard sqlite-jdbc with sqlite-jdbc-crypt for encryption support
// Using Willena's fork (io.github.willena:sqlite-jdbc) which provides SQLCipher support
// via SQLite3 Multiple Ciphers
configurations.matching { !it.name.contains("test", ignoreCase = true) }.all {
    exclude(group = "org.xerial", module = "sqlite-jdbc")
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
        withDeviceTest { }
    }
    iosArm64()
    iosSimulatorArm64()
    wasmJs {
        browser()
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(libs.sqlDelight.runtime)
            implementation(libs.sqlDelight.coroutinesExt)
            implementation(projects.thirdparty.app.cash.sqldelight.paging3)
            implementation(libs.androidx.paging.common)
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.core)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(projects.core.test)
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.test)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.sqlDelight.android)
            implementation(libs.commonsware.saferoom)
            implementation(libs.android.sqlcipher)
        }
        val androidHostTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.bundles.mockito)
                implementation(libs.sqlDelight.jvm)
            }
        }
        val androidDeviceTest by getting {
            dependencies {
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.androidx.test.runner)
            }
        }
        iosMain.dependencies {
            implementation(libs.sqlDelight.native)
        }
        iosTest.dependencies {
        }
        jvmMain.dependencies {
            implementation(libs.sqlDelight.jvm)
            implementation(libs.appdirs)
            implementation(libs.sqlite.jdbc)
        }
        jvmTest.dependencies {
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
            implementation(libs.sqlDelight.web)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(npm("@cashapp/sqldelight-sqljs-worker", libs.versions.sqlDelight.get()))
            implementation(npm("sql.js", "1.8.0"))
        }
        wasmJsTest.dependencies {
        }
    }
    cocoapods {
        summary = "Data library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        pod("SQLCipher", libs.versions.iosSqlCipher.get())
        framework {
            isStatic = false
        }
        if (!OperatingSystem.current().isMacOsX) noPodspec()
    }
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

sqldelight {
    databases {
        create("NoteDb") {
            packageName.set("com.softartdev.notedelight.db")
            generateAsync.set(true)
        }
    }
    linkSqlite.set(false)
}
