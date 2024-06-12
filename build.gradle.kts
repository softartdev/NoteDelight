plugins {
    alias(libs.plugins.gradle.convention) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.cocoapods) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.sqlDelight) apply false
    alias(libs.plugins.mokoResources) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.crashlytics) apply false
}
