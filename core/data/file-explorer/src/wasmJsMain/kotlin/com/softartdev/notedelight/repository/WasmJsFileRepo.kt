package com.softartdev.notedelight.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okio.FileSystem
import okio.Path

class WasmJsFileRepo : AbstractFileRepo() {
    // FileSystem.SYSTEM is not available in wasmJs browser (requires Node.js)
    // This is a stub implementation that throws if accessed
    override val fileSystem: FileSystem
        get() = throw UnsupportedOperationException("FileSystem not available in wasmJs browser")
    
    override val zeroPath: Path
        get() = throw UnsupportedOperationException("FileSystem not available in wasmJs browser")
    
    override val fileListFlow: Flow<List<String>> = flowOf(listOf("File browsing not supported on Web"))

    override fun goToStartPath() {
        // No-op for wasmJs
    }

    override fun goTo(fileName: String) {
        // No-op for wasmJs
    }
}

