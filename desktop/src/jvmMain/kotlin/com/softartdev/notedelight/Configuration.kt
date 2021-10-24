package com.softartdev.notedelight

import com.arkivanov.essenty.parcelable.Parcelable

sealed class Configuration : Parcelable {
    object List : Configuration()
    data class Details(val itemId: Long) : Configuration()
    object Settings : Configuration()
}