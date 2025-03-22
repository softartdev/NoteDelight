import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.softartdev.notedelight.buildlogic"

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get().toInt())
}
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
}

dependencies {
    compileOnly(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("gradleConvention") {
            id = "com.softartdev.notedelight.buildlogic.convention"
            implementationClass = "GradleConventionPlugin"
        }
    }
}
