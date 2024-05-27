package com.softartdev.notedelight

import kotlinx.serialization.Serializable

@Serializable
sealed class Configuration {

    @Serializable
    data object Splash : Configuration()

    @Serializable
    data object SignIn : Configuration()

    @Serializable
    data object Main : Configuration()

    @Serializable
    data class Details(val itemId: Long) : Configuration()

    @Serializable
    data object Settings : Configuration()
}