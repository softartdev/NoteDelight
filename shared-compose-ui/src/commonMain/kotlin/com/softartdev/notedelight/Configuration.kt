package com.softartdev.notedelight

import com.arkivanov.essenty.parcelable.Parcelable

sealed class Configuration : Parcelable {
    object Splash : Configuration()
    object SignIn : Configuration()
    object Main : Configuration()
    data class Details(val itemId: Long) : Configuration()
    object Settings : Configuration()
}