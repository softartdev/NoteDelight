package com.softartdev.notedelight.repository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import okio.BufferedSource
import okio.FileMetadata
import okio.FileSystem
import okio.Path
import okio.Source
import okio.buffer
import okio.use

abstract class AbstractFileRepo : FileRepo {
    private val logger = Logger.withTag(this::class.simpleName.toString())
    protected abstract val fileSystem: FileSystem
    protected abstract val zeroPath: Path
    private val upFolder = "🔙.."
    private val fileContent = "📖"
    private lateinit var currentFileDir: Path
    private lateinit var currentFiles: List<Path>
    private lateinit var currentFileNames: List<String>
    protected val mutableStateFlow: MutableStateFlow<List<String>> = MutableStateFlow(
        value = listOf("🔁loading...")
    )
    override val fileListFlow: Flow<List<String>> = mutableStateFlow

    override fun goToStartPath() {
        try {
            goTo(path = zeroPath)
        } catch (e: Exception) {
            logger.e(e) { "Error initializing FileRepo at path: $zeroPath" }
            mutableStateFlow.value = listOf("File browsing not supported on this platform")
        }
    }

    override fun goTo(fileName: String) = when (fileName) {
        upFolder -> goTo(path = requireNotNull(currentFileDir.parent))
        fileContent -> mutableStateFlow.update { it + fileContent }
        else -> when (val index: Int = currentFileNames.indexOf(fileName)) {
            -1 -> logger.e { "file not found: $fileName" }
            else -> goTo(path = currentFiles[index])
        }
    }

    private fun goTo(path: Path) {
        logger.d { "📂go to: $path" }
        val metadata: FileMetadata = fileSystem.metadataOrNull(path) ?: return
        if (metadata.isDirectory) {
            currentFileDir = path
            currentFiles = fileSystem.list(dir = path)
            currentFileNames = currentFiles.map { curFile: Path ->
                val curMetadata = fileSystem.metadataOrNull(curFile) ?: return@map ""
                val icon = if (curMetadata.isDirectory) "📁" else "📄"
                return@map "$icon ${curFile.name}"
            }
        } else if (metadata.isRegularFile) {
            currentFileDir = path
            currentFiles = emptyList()
            currentFileNames = listOf(fileContent, readFile(path))
        } else {
            logger.e { "unknown file: $path" }
            mutableStateFlow.update { it + "❌ $path" }
            return
        }
        val absolutePath: Path = if (path.isAbsolute) path else fileSystem.canonicalize(path)
        mutableStateFlow.value = listOf("📂$absolutePath", upFolder) + currentFileNames
    }

    private fun readFile(file: Path): String {
        val stringBuilder = StringBuilder()
        fileSystem.source(file).use { fileSource: Source ->
            fileSource.buffer().use { bufferedFileSource: BufferedSource ->
                stringBuilder.append(bufferedFileSource.readUtf8())
            }
        }
        return stringBuilder.toString()
    }
}

