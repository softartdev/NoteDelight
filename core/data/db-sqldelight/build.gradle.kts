@file:OptIn(
    ExperimentalWasmDsl::class,
    org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class,
)

import com.softartdev.notedelight.excludeSqliteJdbcFromNonTestConfigurations
import com.softartdev.notedelight.configureWasmJsChromeForKarmaTests
import com.softartdev.notedelight.configureWebSqlite3mcWasmResources
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.download)
}

group = "com.softartdev.notedelight"

project.excludeSqliteJdbcFromNonTestConfigurations()

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
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = false
            freeCompilerArgs += "-Xoverride-konan-properties=minVersion.ios=15.0"
        }
        iosTarget.compilations.getByName("main").cinterops.configureEach {
            if (name == "swiftPMImport") {
                compilerOpts("-DSQLITE_HAS_CODEC")
            }
        }
    }
    wasmJs {
        browser()
    }
    swiftPMDependencies {
        iosMinimumDeploymentTarget = "15.0"
        discoverClangModulesImplicitly = false
        swiftPackage(
            url = url("https://github.com/sqlcipher/SQLCipher.swift.git"),
            version = exact(libs.versions.iosSqlCipher.get()),
            products = listOf(product("SQLCipher")),
            importedClangModules = listOf("SQLCipher"),
        )
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
            implementation(projects.core.test.common)
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
        named("wasmJsMain") {
            resources.srcDir(layout.buildDirectory.dir("sqlite"))
        }
        named("wasmJsTest") {
            resources.srcDir(layout.buildDirectory.dir("sqlite"))
        }
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

project.configureWasmJsChromeForKarmaTests()
project.configureWebSqlite3mcWasmResources()
