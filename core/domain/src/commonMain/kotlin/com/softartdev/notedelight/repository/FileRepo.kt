package com.softartdev.notedelight.repository

import kotlinx.coroutines.flow.Flow

interface FileRepo {
    val fileListFlow: Flow<List<String>>
    fun goToStartPath()
    fun goTo(fileName: String)
}
