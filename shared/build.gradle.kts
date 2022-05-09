import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.squareup.sqldelight")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
}

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
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.sqlDelight.coroutinesExt)
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
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
            dependencies {
                implementation(libs.coroutines.android)
                api(libs.sqlDelight.android)
                val sqliteVersion = "2.2.0"
                implementation("androidx.sqlite:sqlite:$sqliteVersion")
                implementation("androidx.sqlite:sqlite-ktx:$sqliteVersion")
                implementation("androidx.sqlite:sqlite-framework:$sqliteVersion")
                api("com.commonsware.cwac:saferoom.x:1.3.0")
                api("net.zetetic:android-database-sqlcipher:4.5.1@aar")
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
                implementation(libs.koin.android)
                implementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
            }
        }
        val androidTest by getting {
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
        val iosMain by creating {
            dependencies {
                implementation(libs.sqlDelight.native)
//                api("io.github.softartdev:sqlcipher-ktn-pod:1.2")
            }
        }
        val iosTest by creating
        val jvmMain by getting {
            dependencies {
                implementation(libs.sqlDelight.jvm)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        /* Main hierarchy */
        jvmMain.dependsOn(commonMain)
        androidMain.dependsOn(commonMain)
        iosMain.dependsOn(commonMain)
        iosX64Main.dependsOn(iosMain)
        iosArm64Main.dependsOn(iosMain)
        iosSimulatorArm64Main.dependsOn(iosMain)

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        /* Test hierarchy */
        jvmTest.dependsOn(commonTest)
        androidTest.dependsOn(commonTest)
        iosTest.dependsOn(commonTest)
        iosX64Test.dependsOn(iosTest)
        iosArm64Test.dependsOn(iosTest)
        iosSimulatorArm64Test.dependsOn(iosTest)
    }
    cocoapods {
        version = "1.0"
        summary = "Common library for the NoteDelight app"
        homepage = "https://github.com/softartdev/NoteDelight"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        useLibraries()
        pod("SQLCipher", "~> 4.4.2")
        framework {
//            isStatic = false
            export(libs.mokoResources)
        }
    }
}
sqldelight {
    database("NoteDb") {
        packageName = "com.softartdev.notedelight.shared.db"
//        linkSqlite = false
    }
}
