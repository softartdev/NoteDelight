@file:OptIn(ExperimentalMaterial3Api::class)

package com.softartdev.notedelight.ui.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.softartdev.notedelight.presentation.files.FilesResult
import com.softartdev.notedelight.presentation.files.FilesViewModel
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.pref_title_file_list
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FileListScreen(
    onBackClick: () -> Unit = {},
    filesViewModel: FilesViewModel
) {
    val fileListState: FilesResult by filesViewModel.resultStateFlow.collectAsState()
    var showPermissionDialog: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(filesViewModel) {
        filesViewModel.updateFiles()
    }
    Box {
        FileListScreenContent(
            fileListState = fileListState,
            onBackClick = onBackClick,
            onItemClicked = filesViewModel::onItemClicked,
            onPermClicked = { showPermissionDialog = true }
        )
        if (showPermissionDialog) {
            PermissionDialog(dismissCallback = { showPermissionDialog = false })
        }
    }
}

@Composable
expect fun PermissionDialog(dismissCallback: () -> Unit)

@Composable
fun FileListScreenContent(
    fileListState: FilesResult,
    onBackClick: () -> Unit = {},
    onItemClicked: (text: String) -> Unit = {},
    onPermClicked: () -> Unit = {},
) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.pref_title_file_list)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                    )
                }
            },
            actions = {
                IconButton(onClick = onPermClicked) {
                    Icon(Icons.Filled.Folder, contentDescription = "Permissions")
                }
            }
        )
    },
    content = { paddingValues ->
        when (fileListState) {
            is FilesResult.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
            is FilesResult.Success -> {
                val fileList: List<String> = fileListState.result
                when {
                    fileList.isEmpty() -> Box(modifier = Modifier.padding(paddingValues)) {
                        Text("No files")
                    }
                    else -> FileList(fileList, onItemClicked, Modifier.padding(paddingValues))
                }
            }
            is FilesResult.Error -> Box(modifier = Modifier.padding(paddingValues)) {
                Text("Error: ${fileListState.error ?: "Unknown error"}")
            }
        }
    }
)

@Composable
fun FileList(
    fileList: List<String>,
    onItemClicked: (text: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LazyColumn(modifier = modifier, state = listState) {
        items(items = fileList) { fileName: String ->
            FileItem(fileName, onItemClicked)
            HorizontalDivider()
        }
    }
}

@Composable
fun FileItem(
    fileName: String,
    onItemClicked: (fileName: String) -> Unit
) = Column(
    modifier = Modifier
        .clickable { onItemClicked(fileName) }
        .fillMaxWidth()
        .padding(4.dp)
        .clearAndSetSemantics { contentDescription = fileName }
) {
    Text(
        text = fileName,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Preview
@Composable
fun FileListScreenContentSuccessPreview() {
    FileListScreenContent(
        fileListState = FilesResult.Success(
            result = listOf(
                "file1.txt",
                "file2.txt",
                "another file.md"
            )
        )
    )
}

@Preview
@Composable
fun FileListScreenContentLoadingPreview() {
    FileListScreenContent(
        fileListState = FilesResult.Loading
    )
}

@Preview
@Composable
fun FileListScreenContentErrorPreview() {
    FileListScreenContent(
        fileListState = FilesResult.Error("Can't read directory")
    )
}

@Preview
@Composable
fun PermissionDialogPreview() {
    PermissionDialog(dismissCallback = {})
}
