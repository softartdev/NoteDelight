package com.softartdev.notedelight

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.disableIosReleaseTasks() {
    val disabledIosReleaseTasks = listOf(
        "linkPodReleaseFrameworkIosArm64",
        "linkPodReleaseFrameworkIosSimulatorArm64",
    )
    tasks.matching { it.name in disabledIosReleaseTasks }.configureEach {
        enabled = false
    }
}
