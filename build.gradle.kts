import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.sqlDelight) apply false
    alias(libs.plugins.room3) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.crashlytics) apply false
}

WasmYarnRootExtension[project].apply {
    // The generated lock reflects only the DB module selected in settings.gradle.kts.
    // Keep it in sync automatically when CORE_DATA_DB_MODULE changes.
    yarnLockMismatchReport = YarnLockMismatchReport.WARNING
    yarnLockAutoReplace = true
    reportNewYarnLock = false
}
