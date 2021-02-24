import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.squareup.sqldelight")
    id("com.android.library")
}
group = "com.softartdev.notedelight.shared"
version = "1.0"
android {
    configurations {
//        create("androidTestApi")
//        create("androidTestDebugApi")
//        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}
kotlin {
    android()
    ios()
    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")
                implementation("com.squareup.sqldelight:coroutines-extensions:${rootProject.extra["sqldelight_version"]}")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
                api("com.squareup.okio:okio-multiplatform:2.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutines_version"]}")
                api("com.squareup.sqldelight:android-driver:${rootProject.extra["sqldelight_version"]}")
                val sqliteVersion = "2.1.0"
                implementation("androidx.sqlite:sqlite:$sqliteVersion")
                implementation("androidx.sqlite:sqlite-ktx:$sqliteVersion")
                implementation("androidx.sqlite:sqlite-framework:$sqliteVersion")
                api("com.commonsware.cwac:saferoom.x:1.3.0")
                api("net.zetetic:android-database-sqlcipher:4.4.2@aar")
                implementation("com.jakewharton.timber:timber:${rootProject.extra["timber_version"]}")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(project(":shared-android-test-util"))
                implementation("junit:junit:${rootProject.extra["junit_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines_version"]}")
                implementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")
                implementation("org.mockito:mockito-inline:${rootProject.extra["mockito_version"]}")
                implementation("com.squareup.sqldelight:sqlite-driver:${rootProject.extra["sqldelight_version"]}")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("co.touchlab:sqliter:0.7.1")
                implementation("com.squareup.sqldelight:native-driver:${rootProject.extra["sqldelight_version"]}")
                implementation("co.touchlab:sqliter:0.7.1") {
                    version {
                        strictly("0.7.1")
                    }
                }
            }
        }
        val iosTest by getting
    }
    cocoapods {
//        frameworkName = "SharedCode"
        summary = "Common library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        pod("SQLCipher", "~> 4.0")
        framework {
            isStatic = false
//            export(Deps.kermit)
            transitiveExport = true
        }
//        useLibraries()
    }
    targets.filterIsInstance<KotlinNativeTarget>()
        .map(KotlinNativeTarget::binaries)
        .filterIsInstance<Framework>()
        .forEach {
            it.isStatic = false
            it.linkerOpts.add("-lsqlite3")
        }
}
sqldelight {
    database("NoteDb") {
        packageName = "com.softartdev.notedelight.shared.db"
//        linkSqlite = false
    }
}
android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=org.mylibrary.OptInAnnotation"
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
        exclude("**/attach_hotspot_windows.dll")
        exclude("META-INF/licenses/**")
        pickFirst("META-INF/AL2.0")
        pickFirst("META-INF/LGPL2.1")
    }
}

fun CocoapodsExtension.framework(configuration: Framework.() -> Unit) {
    kotlin.targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            configuration()
        }
    }
}
