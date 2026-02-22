@file:OptIn(ExperimentalWasmDsl::class)

import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
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

// Chrome binary for Karma tests.
// Auto-detects Chrome on macOS/Linux/Windows when CHROME_BIN is not set.
// NOTE: WasmGC module compilation in Chrome is very slow for large modules (~30 MB test WASM).
// The main() function is guarded with isKarmaTestRunner() check to prevent the production app
// from launching during tests (see main.kt).
val defaultChromePaths: List<String> = listOf(
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",          // macOS
    "/usr/bin/google-chrome-stable",                                         // Linux (stable)
    "/usr/bin/google-chrome",                                                // Linux
    "/usr/bin/chromium-browser",                                             // Linux (Chromium)
    "/usr/bin/chromium",                                                     // Linux (Chromium alt)
    "C:/Program Files/Google/Chrome/Application/chrome.exe",                 // Windows
    "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe",           // Windows x86
)
val chromeBinary: String? = providers.environmentVariable("CHROME_BIN").orNull
    ?: defaultChromePaths.firstOrNull { file(it).exists() }

tasks.named<KotlinJsTest>("wasmJsBrowserTest").configure {
    enabled = chromeBinary != null
    if (chromeBinary != null) {
        environment("CHROME_BIN", chromeBinary)
    }
}

// SQLite3MultipleCiphers WASM build with encryption support
// See https://github.com/utelle/SQLite3MultipleCiphers/releases for the latest version
val sqlite3mcVersion = "2.2.7"
val sqliteVersion = "3.51.2"
val sqliteWasmVersion = "3510200"
val sqlite3mcZip = "sqlite3mc-$sqlite3mcVersion-sqlite-$sqliteVersion-wasm.zip"
val sqliteDownload: Provider<Download> = tasks.register("sqliteDownload", Download::class.java) {
    src("https://github.com/utelle/SQLite3MultipleCiphers/releases/download/v$sqlite3mcVersion/$sqlite3mcZip")
    dest(layout.buildDirectory.dir("tmp"))
    onlyIfModified(true)
}
val sqliteUnzip: TaskProvider<Copy> = tasks.register("sqliteUnzip", Copy::class.java) {
    dependsOn(sqliteDownload)
    from(zipTree(layout.buildDirectory.dir("tmp/$sqlite3mcZip"))) {
        include("sqlite3mc-wasm-$sqliteWasmVersion/jswasm/**")
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
tasks.named<Copy>("wasmJsTestProcessResources").configure {
    dependsOn(sqliteUnzip)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Sync>("wasmJsBrowserDistribution").configure {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
