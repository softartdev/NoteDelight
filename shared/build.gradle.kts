import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.TEST_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_MAIN_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_TEST_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.android.library)
    alias(libs.plugins.mokoResources)
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
        kotlinOptions.jvmTarget = libs.versions.jdk.get()
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
    resourcesPackage.set("com.softartdev.notedelight")
}
kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
    jvm()
    androidTarget()
    iosIntermediateSourceSets(iosArm64(), iosSimulatorArm64())
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.sqlDelight.coroutinesExt)
            api(libs.kotlinx.datetime)
            api(libs.napier)
            api(libs.mokoResources)
            implementation(libs.koin.core)
            api(libs.material.theme.prefs)
            implementation(libs.stately.common)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(libs.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.mokoResources.test)
        }
        androidMain {
            dependsOn(commonMain.get())//TODO remove after update moko-resources > 0.23.0
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
            dependsOn(commonTest.get())
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
                implementation(libs.coroutines.test)
                implementation(libs.bundles.mockito)
                implementation(libs.sqlDelight.jvm)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.turbine)
            }
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
            export(libs.mokoResources)
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

//FIXME https://github.com/cashapp/sqldelight/issues/4523
fun KotlinSourceSetContainer.iosIntermediateSourceSets(vararg iosTargets: KotlinNativeTarget) {
    val children: List<Pair<KotlinSourceSet, KotlinSourceSet>> = iosTargets.map { target ->
        val main = target.compilations.getByName(MAIN_COMPILATION_NAME).defaultSourceSet
        val test = target.compilations.getByName(TEST_COMPILATION_NAME).defaultSourceSet
        return@map main to test
    }
    val parent: Pair<KotlinSourceSet, KotlinSourceSet> = Pair(
        first = sourceSets.getByName(COMMON_MAIN_SOURCE_SET_NAME),
        second = sourceSets.getByName(COMMON_TEST_SOURCE_SET_NAME)
    )
    createIntermediateSourceSet("iosMain", children.map { it.first }, parent.first)
    createIntermediateSourceSet("iosTest", children.map { it.second }, parent.second)
}

fun KotlinSourceSetContainer.createIntermediateSourceSet(
    name: String,
    children: List<KotlinSourceSet>,
    parent: KotlinSourceSet
): KotlinSourceSet = sourceSets.maybeCreate(name).apply {
    dependsOn(parent)
    children.forEach { it.dependsOn(this) }
}

tasks.named("generateMRandroidUnitTest") {
    dependsOn(tasks.named("generateDebugLintModel"))
    dependsOn(tasks.named("generateDebugLintReportModel"))
    dependsOn(tasks.named("generateReleaseLintModel"))
}//TODO remove after update moko-resources > 0.24.0-alpha-2
