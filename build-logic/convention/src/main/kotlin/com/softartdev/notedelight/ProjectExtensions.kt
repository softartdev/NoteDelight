package com.softartdev.notedelight

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.TEST_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_MAIN_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_TEST_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

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

private fun KotlinSourceSetContainer.createIntermediateSourceSet(
    name: String,
    children: List<KotlinSourceSet>,
    parent: KotlinSourceSet
): KotlinSourceSet = sourceSets.maybeCreate(name).apply {
    dependsOn(parent)
    children.forEach { it.dependsOn(this) }
}
