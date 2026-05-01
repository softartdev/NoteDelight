package com.softartdev.notedelight.ui.settings.detail

class TestDatabaseFilePicker : DatabaseFilePicker {
    private val exportQueue = ArrayDeque<String>()
    private val importQueue = ArrayDeque<String>()

    fun setQueues(exportPaths: List<String>, importPaths: List<String>) {
        exportQueue.clear()
        exportQueue.addAll(exportPaths)
        importQueue.clear()
        importQueue.addAll(importPaths)
    }

    override fun launchExport(defaultFileName: String, onPicked: (String?) -> Unit) {
        onPicked(exportQueue.removeFirst())
    }

    override fun launchImport(onPicked: (String?) -> Unit) {
        onPicked(importQueue.removeFirst())
    }
}
