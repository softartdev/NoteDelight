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
    defaultConfig {
        minSdk = libs.versions.oldMinSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
    packagingOptions.resources {
        excludes += setOf(
            "META-INF/*.kotlin_module", "**/attach_hotspot_windows.dll", "META-INF/licenses/**"
        )
        pickFirsts += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
}
multiplatformResources {
    multiplatformResourcesPackage = "com.softartdev.notedelight"
}
kotlin {
    jvm()
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.sqlDelight.coroutinesExt)
                api(libs.kotlinx.datetime)
                api(libs.napier)
                api(libs.mokoResources)
                implementation(libs.koin.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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
        val androidTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(project(":shared-android-test-util"))
                implementation(libs.junit)
                implementation(libs.coroutines.test)
                implementation(libs.bundles.mockito)
                implementation(libs.sqlDelight.jvm)
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
    }
    cocoapods {
        summary = "Common library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
//        useLibraries()
//        pod("SQLCipher", "~> 4.5.2")
        framework {
//            isStatic = false
            export(libs.mokoResources)
            export("io.github.softartdev:sqlcipher-ktn-pod:1.3")
        }
    }
}
sqldelight {
    database("NoteDb") {
        packageName = "com.softartdev.notedelight.shared.db"
//        linkSqlite = false
    }
}
