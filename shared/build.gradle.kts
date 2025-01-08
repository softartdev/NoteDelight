import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.gradle.convention)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.android.library)
}
version = "1.0"

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDir(
        File(layout.buildDirectory.get().asFile, "generated/moko/androidMain/res")
    )
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
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
        compilerOptions.freeCompilerArgs.add("-Xopt-in=kotlin.RequiresOptIn")
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
kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm()
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.core)
            implementation(libs.sqlDelight.coroutinesExt)
            implementation(libs.sqlDelight.paging)
            api(libs.kotlinx.datetime)
            api(libs.napier)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.core.viewmodel)
            api(libs.material.theme.prefs)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.test)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.test)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.android)
            api(libs.sqlDelight.android)
            implementation(libs.bundles.androidx.sqlite)
            api(libs.commonsware.saferoom)
            api(libs.android.sqlcipher)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.android)
            implementation(libs.espresso.idling.resource)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-junit"))
            implementation(libs.junit)
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.test)
            implementation(libs.bundles.mockito)
            implementation(libs.sqlDelight.jvm)
            implementation(libs.androidx.arch.core.testing)
            implementation(libs.turbine)
        }
        iosMain.dependencies {
            implementation(libs.sqlDelight.native)
        }
        jvmMain.dependencies {
            implementation(libs.sqlDelight.jvm)
            implementation(libs.appdirs)
        }
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-junit"))
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.test)
            implementation(project.dependencies.platform(libs.coroutines.bom))
            implementation(libs.coroutines.swing)
        }
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
    cocoapods {
        summary = "Common library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        ios.deploymentTarget = "14.0"
        pod("SQLCipher", libs.versions.iosSqlCipher.get())
        framework {
            isStatic = true
        }
        if (!OperatingSystem.current().isMacOsX) noPodspec()
    }
}
sqldelight {
    databases {
        create("NoteDb") {
            packageName.set("com.softartdev.notedelight.shared.db")
        }
    }
    linkSqlite.set(false)
}
