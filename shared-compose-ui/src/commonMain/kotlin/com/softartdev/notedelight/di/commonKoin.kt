package com.softartdev.notedelight.di

import com.softartdev.notedelight.shared.base.KmmViewModel

//@Composable FIXME revert after fix https://youtrack.jetbrains.com/issue/KT-53523
expect inline fun <reified T : KmmViewModel> getViewModel(): T