import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.squareup.sqldelight")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
}
group = "com.softartdev.notedelight.shared"
version = "1.0"

android {
    compileSdk = 31
    defaultConfig {
        minSdk = 16
        targetSdk = 31
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
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
    packagingOptions.resources {
        excludes.addAll(listOf(
            "META-INF/*.kotlin_module",
            "**/attach_hotspot_windows.dll",
            "META-INF/licenses/**"
        ))
        pickFirsts.addAll(listOf(
            "META-INF/AL2.0",
            "META-INF/LGPL2.1"
        ))
    }
}
kotlin {
    jvm()
    android()
    ios()
    iosSimulatorArm64("ios")
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines_version"]}")
                implementation("com.squareup.sqldelight:coroutines-extensions:${rootProject.extra["sqldelight_version"]}")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
                api("com.squareup.okio:okio-multiplatform:2.9.0")
                api("io.github.aakira:napier:${rootProject.extra["napierVersion"]}")
                api("dev.icerock.moko:resources:${rootProject.extra["moko_resources_version"]}")
                implementation("io.insert-koin:koin-core:${rootProject.extra["koin_version"]}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.insert-koin:koin-test:${rootProject.extra["koin_version"]}")
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
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
                implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")
                implementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
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
                implementation("com.squareup.sqldelight:native-driver:${rootProject.extra["sqldelight_version"]}")
                api("io.github.softartdev:sqlcipher-ktn-pod:1.2")
            }
        }
        val iosTest by getting
        val jvmMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:sqlite-driver:${rootProject.extra["sqldelight_version"]}")
            }
        }
        val jvmTest by getting {
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
        useLibraries()
//        pod("SQLCipher", "~> 4.4.2")
        framework {
//            isStatic = false
            export("dev.icerock.moko:resources-iosx64:${rootProject.extra["moko_resources_version"]}")
        }
    }
}
sqldelight {
    database("NoteDb") {
        packageName = "com.softartdev.notedelight.shared.db"
//        linkSqlite = false
    }
}
multiplatformResources {
    multiplatformResourcesPackage = "com.softartdev.notedelight" // required
}
