package com.softartdev.notedelight

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import java.util.Properties
import java.io.File

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.excludeSqliteJdbcFromNonTestConfigurations() {
    configurations
        .matching { !it.name.contains("test", ignoreCase = true) }
        .configureEach {
            exclude(mapOf("group" to "org.xerial", "module" to "sqlite-jdbc"))
        }
}

fun Project.configureWasmJsChromeForKarmaTests(defaultChromePaths: List<String> = DEFAULT_CHROME_PATHS) {
    val chromeBinary: String? = providers.environmentVariable("CHROME_BIN").orNull
        ?: defaultChromePaths.firstOrNull { file(it).exists() }

    tasks.named("wasmJsBrowserTest", KotlinJsTest::class.java).configure {
        enabled = chromeBinary != null
        chromeBinary?.let { environment("CHROME_BIN", it) }
    }
}

fun Project.configureWebSqlite3mcWasmResources(
    sqlite3mcVersion: String = SQLITE3MC_VERSION,
    sqliteVersion: String = SQLITE_VERSION,
    sqliteWasmVersion: String = SQLITE_WASM_VERSION,
) {
    val sqlite3mcZip = "sqlite3mc-$sqlite3mcVersion-sqlite-$sqliteVersion-wasm.zip"
    val sqliteCacheFile = File(
        gradle.gradleUserHomeDir,
        "caches/notedelight/sqlite3mc/$sqlite3mcVersion/$sqlite3mcZip",
    )
    val sqliteDownload = rootProject.tasks.findByName(SQLITE_DOWNLOAD_TASK_NAME)?.let {
        rootProject.tasks.named(SQLITE_DOWNLOAD_TASK_NAME, Download::class.java)
    } ?: rootProject.tasks.register(SQLITE_DOWNLOAD_TASK_NAME, Download::class.java) {
        src("https://github.com/utelle/SQLite3MultipleCiphers/releases/download/v$sqlite3mcVersion/$sqlite3mcZip")
        dest(sqliteCacheFile)
        onlyIf { !sqliteCacheFile.isFile }
    }
    val sqliteUnzip = tasks.register("sqliteUnzip", Copy::class.java) {
        dependsOn(sqliteDownload)
        from(zipTree(sqliteCacheFile)) {
            include("sqlite3mc-wasm-$sqliteWasmVersion/jswasm/**")
            exclude("**/*worker1*")
            eachFile {
                relativePath = RelativePath(true, *relativePath.segments.drop(2).toTypedArray())
            }
        }
        into(layout.buildDirectory.dir("sqlite"))
        includeEmptyDirs = false
    }
    tasks.named("wasmJsProcessResources").configure {
        dependsOn(sqliteUnzip)
    }
    tasks.named("wasmJsTestProcessResources", Copy::class.java).configure {
        dependsOn(sqliteUnzip)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    tasks.withType(Sync::class.java)
        .matching { it.name == "wasmJsBrowserDistribution" }
        .configureEach { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
}

fun Project.configureSwiftPmOfflineResolution() {
    tasks.configureEach {
        when {
            name == "fetchSyntheticImportProjectPackages" ||
                name.startsWith("fetchUmbrellaPackageIdentifierFor") -> {
                addArgumentsToInternalListProperty(
                    getterName = "getAdditionalSwiftPackageResolveArgs",
                    arguments = listOf("--skip-update", "--disable-automatic-resolution"),
                )
            }

            name.startsWith("convertSyntheticImportProjectIntoDefFile") -> {
                addArgumentsToInternalListProperty(
                    getterName = "getAdditionalXcodeArgs",
                    arguments = listOf("-skipPackageUpdates", "-onlyUsePackageVersionsFromResolvedFile"),
                )
            }
        }
    }
}

private fun Task.addArgumentsToInternalListProperty(
    getterName: String,
    arguments: List<String>,
) {
    val getter = javaClass.methods.single { method ->
        method.name == getterName && method.parameterCount == 0
    }
    @Suppress("UNCHECKED_CAST")
    val property = getter.invoke(this) as ListProperty<String>
    property.addAll(arguments)
}

fun Project.forceAndroidXDependencyVersions() {
    val lifecycleVersion: String = libs.findVersion("androidxLifecycle").get().requiredVersion
    val lifecycleModules: List<String> = listOf("common", "common-java8", "runtime", "runtime-ktx", "runtime-compose", "viewmodel", "viewmodel-ktx", "viewmodel-compose", "viewmodel-savedstate", "livedata", "livedata-core", "livedata-core-ktx", "process")
    configurations.all {
        resolutionStrategy {
            lifecycleModules.forEach { depName: String ->
                force("androidx.lifecycle:lifecycle-$depName:$lifecycleVersion")
            }
            force("androidx.savedstate:savedstate:1.4.0")
            force("androidx.savedstate:savedstate-ktx:1.4.0")
            force("androidx.savedstate:savedstate-compose:1.4.0")
            force("androidx.concurrent:concurrent-futures:1.2.0")
            force("com.google.errorprone:error_prone_annotations:2.30.0")
        }
    }
}

fun Project.configureJvmVersionProps(version: String?) {
    val type: Class<GenerateVersionPropertiesTask> = GenerateVersionPropertiesTask::class.java
    val file = layout.buildDirectory.file("generated/desktop-version/version.properties")
    val generateVersionProperties = tasks.register("generateVersionProperties", type) {
        this.version.set(version)
        this.outputFile.set(file)
    }
    tasks.named("jvmProcessResources", Copy::class.java).configure {
        dependsOn(generateVersionProperties)
        from(generateVersionProperties.flatMap(GenerateVersionPropertiesTask::outputFile))
    }
}

abstract class GenerateVersionPropertiesTask : DefaultTask() {
    @get:Input
    abstract val version: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        val properties = Properties()
        properties.setProperty("version", version.get())
        properties.store(file.outputStream(), null)
    }
}

private const val SQLITE3MC_VERSION: String = "2.2.7"
private const val SQLITE_VERSION: String = "3.51.2"
private const val SQLITE_WASM_VERSION: String = "3510200"
private const val SQLITE_DOWNLOAD_TASK_NAME: String = "downloadSqlite3mcWasm"

private val DEFAULT_CHROME_PATHS: List<String> = listOf(
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    "/usr/bin/google-chrome-stable",
    "/usr/bin/google-chrome",
    "/usr/bin/chromium-browser",
    "/usr/bin/chromium",
    "C:/Program Files/Google/Chrome/Application/chrome.exe",
    "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe",
)
