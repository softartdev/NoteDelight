@file:OptIn(ExperimentalWasmDsl::class)

import com.softartdev.notedelight.configureWasmJsChromeForKarmaTests
import com.softartdev.notedelight.configureWebSqlite3mcWasmResources
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.download)
}

kotlin {
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath: String = project.rootDir.path
            val projectDirPath: String = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    // Serve sources to debug inside the browser
                    static(rootDirPath)
                    static(projectDirPath)
                }
            }
            testTask { useKarma { useChromeHeadless() } }
        }
        binaries.executable()
    }
    sourceSets {
        wasmJsMain {
            dependencies {
                implementation(projects.core.domain)
                implementation(projects.core.presentation)
                implementation(projects.ui.shared)
                implementation(libs.compose.ui)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.kermit)
            }
            resources.srcDir(layout.buildDirectory.dir("sqlite"))
        }
        wasmJsTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.ui.test)
                implementation(libs.compose.ui.test)
                implementation(libs.compose.material3)
                implementation(libs.compose.material.icons.extended)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.androidx.lifecycle.runtime.testing)
                implementation(libs.androidx.paging.common)
            }
            resources.srcDir(layout.buildDirectory.dir("sqlite"))
        }
    }
}

project.configureWasmJsChromeForKarmaTests()
project.configureWebSqlite3mcWasmResources()
