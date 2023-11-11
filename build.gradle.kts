plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.parcelize).apply(false)
    alias(libs.plugins.kotlin.cocoapods).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.sqlDelight).apply(false)
    alias(libs.plugins.mokoResources).apply(false)
    alias(libs.plugins.gms).apply(false)
    alias(libs.plugins.crashlytics).apply(false)
}

extra["hasXcode15"] = hasXcode15() //TODO: remove after update Kotlin >= 1.9.10

fun hasXcode15(): Boolean = try {
    val process: Process = ProcessBuilder("xcodebuild", "-version").start()
    process.inputStream.bufferedReader().use { reader ->
        process.waitFor() == 0 && reader.readText().startsWith("Xcode 15.")
    }
} catch (t: Throwable) {
    t.printStackTrace()
    false
}
