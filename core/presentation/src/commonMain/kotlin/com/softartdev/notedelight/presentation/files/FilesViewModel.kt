package com.softartdev.notedelight.presentation.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.repository.FileRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FilesViewModel(private val fileRepo: FileRepo) : ViewModel() {
    private val logger = Logger.withTag(this@FilesViewModel::class.simpleName.toString())
    private val mutableStateFlow: MutableStateFlow<FilesResult> = MutableStateFlow(
        value = FilesResult.Loading
    )
    val resultStateFlow: StateFlow<FilesResult> = mutableStateFlow
    var job: Job? = null

    fun updateFiles() {
        job?.cancel()
        job = viewModelScope.launch {
            fileRepo.fileListFlow
                .map(FilesResult::Success)
                .collect(mutableStateFlow::emit)
        }
        try {
            fileRepo.goToStartPath()
        } catch (e: Throwable) {
            logger.e(e) { "Error goToStartPath" }
            mutableStateFlow.value = FilesResult.Error(e.message)
        }
    }

    fun onItemClicked(fileName: String) = viewModelScope.launch {
        try {
            fileRepo.goTo(fileName)
        } catch (e: Throwable) {
            logger.e(e) { "Error onItemClicked: $fileName" }
            mutableStateFlow.value = FilesResult.Error(e.message)
        }
    }
}
