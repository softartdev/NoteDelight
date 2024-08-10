package com.softartdev.notedelight.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

@Composable
expect inline fun <reified T : ViewModel> getViewModel(): T