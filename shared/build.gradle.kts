@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.squareup.sqldelight")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
}
version = "1.0"

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDir(File(buildDir, "generated/moko/androidMain/res"))
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
    packagingOptions.resources {
        excludes += setOf(
            "META-INF/*.kotlin_module", "**/attach_hotspot_windows.dll", "META-INF/licenses/**"
        )
        pickFirsts += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
    testOptions.unitTests.isReturnDefaultValues = true
    namespace = "com.softartdev.notedelight.shared"
}
multiplatformResources {
    multiplatformResourcesPackage = "com.softartdev.notedelight"
}
kotlin {
    jvmToolchain(11)
    jvm()
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.sqlDelight.coroutinesExt)
                api(libs.kotlinx.datetime)
                api(libs.napier)
                api(libs.mokoResources)
                implementation(libs.koin.core)
                api(libs.material.theme.prefs)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
                implementation(libs.koin.test)
                implementation(libs.mokoResources.test)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.coroutines.android)
                api(libs.sqlDelight.android)
                implementation(libs.bundles.androidx.sqlite)
                api(libs.commonsware.saferoom)
                api(libs.android.sqlcipher)
                api(libs.androidx.lifecycle.viewmodel)
                implementation(libs.koin.android)
                implementation(libs.espresso.idling.resource)
            }
        }
        val androidUnitTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(project(":shared-android-test-util"))
                implementation(libs.junit)
                implementation(libs.coroutines.test)
                implementation(libs.bundles.mockito)
                implementation(libs.sqlDelight.jvm)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.turbine)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.sqlDelight.native)
                api("io.github.softartdev:sqlcipher-ktn-pod:1.3")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.sqlDelight.jvm)
            }
        }
        val jvmTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
    cocoapods {
        summary = "Common library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        ios.deploymentTarget = "14.0"
//        podfile = project.file("../iosApp/Podfile")
//        useLibraries()
//        pod("SQLCipher", "~> 4.5.2")
        framework {
            isStatic = true
            export(libs.mokoResources)
            export("io.github.softartdev:sqlcipher-ktn-pod:1.3")
        }
    }
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += listOf(
                "-linker-option", "-framework", "-linker-option", "Metal",
                "-linker-option", "-framework", "-linker-option", "CoreText",
                "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                // TODO: the current compose binary surprises LLVM, so disable checks for now.
                "-Xdisable-phases=VerifyBitcode"
            )
        }
    }
}
sqldelight {
    database("NoteDb") {
        packageName = "com.softartdev.notedelight.shared.db"
//        linkSqlite = false
    }
}
//TODO try to remove after update moko-resources version > 0.21.1
tasks.named("jvmProcessResources") {
    dependsOn(":shared:generateMRjvmMain")
    dependsOn(":shared:generateMRcommonMain")
}
tasks.named("iosSimulatorArm64ProcessResources") {
    dependsOn(":shared:generateMRiosSimulatorArm64Main")
    dependsOn(":shared:generateMRcommonMain")
}
tasks.named("iosX64ProcessResources") {
    dependsOn(":shared:generateMRiosX64Main")
    dependsOn(":shared:generateMRcommonMain")
}