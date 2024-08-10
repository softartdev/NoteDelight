package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import org.koin.java.KoinJavaComponent.get

@Composable
actual inline fun <reified T : ViewModel> getViewModel(): T = remember { get(T::class.java) }