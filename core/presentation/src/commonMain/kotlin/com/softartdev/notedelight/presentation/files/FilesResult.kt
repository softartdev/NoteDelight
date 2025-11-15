package com.softartdev.notedelight.presentation.files

sealed class FilesResult {
    object Loading : FilesResult()
    data class Success(val result: List<String>) : FilesResult()
    data class Error(val error: String? = null) : FilesResult()
}

