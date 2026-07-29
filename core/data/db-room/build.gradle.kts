@file:OptIn(
    org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class,
    org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class,
)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.softartdev.notedelight.configureWasmJsChromeForKarmaTests
import com.softartdev.notedelight.configureWebSqlite3mcWasmResources

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.room3)
    alias(libs.plugins.download)
}

group = "com.softartdev.notedelight"

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm()
    wasmJs {
        browser()
    }
    android {
        namespace = "com.softartdev.notedelight.core.data"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
        }
        withHostTest { }
    }
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = true
            freeCompilerArgs += "-Xoverride-konan-properties=minVersion.ios=15.0"
        }
        iosTarget.compilations.getByName("main").cinterops.configureEach {
            if (name == "swiftPMImport") {
                compilerOpts("-DSQLITE_HAS_CODEC")
            }
        }
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
            implementation(libs.room3.runtime)
            implementation(libs.room3.paging)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.paging.common)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(projects.core.test.common)
            implementation(libs.room3.testing)
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.test)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.commonsware.saferoom)
            implementation(libs.android.sqlcipher)
            implementation(libs.androidx.sqlite.framework)
        }
        named("androidHostTest") {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.bundles.mockito)
                implementation(libs.androidx.sqlite.bundled)
            }
        }
        iosMain.dependencies {
        }
        iosTest.dependencies {
        }
        jvmMain.dependencies {
            implementation(libs.appdirs)
            implementation(libs.kermit)
            implementation(libs.sqlite.jdbc)
        }
        jvmTest.dependencies {
        }
        wasmJsMain.dependencies {
            implementation(libs.androidx.sqlite.web)
            implementation(libs.kotlinx.browser)
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

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room3.compiler)
    add("kspJvm", libs.room3.compiler)
    add("kspIosSimulatorArm64", libs.room3.compiler)
    add("kspIosArm64", libs.room3.compiler)
    add("kspWasmJs", libs.room3.compiler)
    coreLibraryDesugaring(libs.desugar)
}

project.configureWasmJsChromeForKarmaTests()
project.configureWebSqlite3mcWasmResources()
