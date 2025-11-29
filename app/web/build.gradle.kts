@file:OptIn(ExperimentalWasmDsl::class)

import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.download)
}

kotlin {
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
//                    open = false  // Disable automatic browser opening
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(projects.core.presentation)
                implementation(projects.ui.shared)
                implementation(compose.ui)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.kermit)
            }
            resources.srcDir(layout.buildDirectory.dir("sqlite"))
        }
        val wasmJsTest by getting
    }
}

val sqliteVersion = 3500400 // See https://sqlite.org/download.html for the latest wasm build version
val sqliteDownload = tasks.register("sqliteDownload", Download::class.java) {
    src("https://sqlite.org/2025/sqlite-wasm-$sqliteVersion.zip")
    dest(layout.buildDirectory.dir("tmp"))
    onlyIfModified(true)
}
val sqliteUnzip = tasks.register("sqliteUnzip", Copy::class.java) {
    dependsOn(sqliteDownload)
    from(zipTree(layout.buildDirectory.dir("tmp/sqlite-wasm-$sqliteVersion.zip"))) {
        include("sqlite-wasm-$sqliteVersion/jswasm/**")
        exclude("**/*worker1*") // We use our own worker
        eachFile {
            relativePath = RelativePath(true, *relativePath.segments.drop(2).toTypedArray())
        }
    }
    into(layout.buildDirectory.dir("sqlite"))
    includeEmptyDirs = false
}
// Hook the unzip task into the wasmJs resource processing
tasks.named("wasmJsProcessResources").configure {
    dependsOn(sqliteUnzip)
}
