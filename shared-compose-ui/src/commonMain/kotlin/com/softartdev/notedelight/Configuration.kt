package com.softartdev.notedelight

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

sealed class Configuration : Parcelable {

    @Parcelize
    object Splash : Configuration()

    @Parcelize
    object SignIn : Configuration()

    @Parcelize
    object Main : Configuration()

    @Parcelize
    data class Details(val itemId: Long) : Configuration()

    @Parcelize
    object Settings : Configuration()
}