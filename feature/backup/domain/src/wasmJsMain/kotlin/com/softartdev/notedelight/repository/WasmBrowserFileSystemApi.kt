@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.repository

import kotlin.js.JsAny
import kotlin.js.Promise

@JsFun(
    "() => typeof window !== 'undefined' && " +
        "(typeof window.showDirectoryPicker === 'function' || typeof window.showSaveFilePicker === 'function')"
)
external fun wasmSupportsExportPicker(): Boolean

@JsFun("() => typeof window !== 'undefined' && typeof window.showOpenFilePicker === 'function'")
external fun wasmSupportsOpenFilePicker(): Boolean

@JsFun(
    """
    (defaultFileName) => {
      const exportPickerId = "notedelight-db-export-save-v4";
      const exportDirectoryPickerId = "notedelight-db-export-dir-v4";
      const savePickerOptions = [
        {
          id: exportPickerId,
          suggestedName: defaultFileName,
          startIn: "downloads",
          types: [
            {
              description: "SQLite database",
              accept: { "application/octet-stream": [".db"] },
            },
          ],
        },
        {
          id: exportPickerId,
          suggestedName: defaultFileName,
          types: [
            {
              description: "SQLite database",
              accept: { "application/octet-stream": [".db"] },
            },
          ],
        },
        { id: exportPickerId, suggestedName: defaultFileName },
      ];
      const pickerOptions = [
        { id: exportDirectoryPickerId, mode: "readwrite", startIn: "downloads" },
        { id: exportDirectoryPickerId, mode: "readwrite", startIn: "documents" },
        { mode: "readwrite" },
        undefined,
      ];
      const shouldRetry = (error) => {
        const errorName = error && typeof error.name === "string" ? error.name : "";
        return errorName !== "AbortError" && errorName !== "NotAllowedError";
      };
      const pickWithSaveDialog = (index) => {
        const options = savePickerOptions[index];
        try {
          console.info("[NoteDelight] export picker: trying showSaveFilePicker", index, options);
          return window.showSaveFilePicker(options).catch((error) => {
            if (!shouldRetry(error) || index >= savePickerOptions.length - 1) {
              throw error;
            }
            return pickWithSaveDialog(index + 1);
          });
        } catch (error) {
          if (!shouldRetry(error) || index >= savePickerOptions.length - 1) {
            throw error;
          }
          return pickWithSaveDialog(index + 1);
        }
      };
      const pickFromDirectory = (index) => {
        const options = pickerOptions[index];
        try {
          console.info("[NoteDelight] export picker: trying showDirectoryPicker", index, options);
          return window.showDirectoryPicker(options)
            .then((directoryHandle) => directoryHandle.getFileHandle(defaultFileName, { create: true }))
            .catch((error) => {
              if (!shouldRetry(error) || index >= pickerOptions.length - 1) {
                throw error;
              }
              return pickFromDirectory(index + 1);
            });
        } catch (error) {
          if (!shouldRetry(error) || index >= pickerOptions.length - 1) {
            throw error;
          }
          return pickFromDirectory(index + 1);
        }
      };
      const savePickerSupported = typeof window.showSaveFilePicker === "function";
      const directoryPickerSupported = typeof window.showDirectoryPicker === "function";
      if (savePickerSupported) {
        console.info("[NoteDelight] export picker: using save-file dialog");
        return pickWithSaveDialog(0);
      }
      if (directoryPickerSupported) {
        console.info("[NoteDelight] export picker: save-file unavailable, using directory picker");
        return pickFromDirectory(0);
      }
      console.info("[NoteDelight] export picker: no supported API");
      return Promise.reject(new Error("Export picker is not supported"));
    }
    """
)
external fun wasmPickExportFileHandle(defaultFileName: String): Promise<JsAny?>

@JsFun("() => window.showOpenFilePicker()")
external fun wasmShowOpenFilePicker(): Promise<JsAny?>

@JsFun("(items) => Array.isArray(items) && items.length > 0 ? items[0] : null")
external fun wasmFirstArrayItem(items: JsAny?): JsAny?

@JsFun("(handle) => (handle && typeof handle.name === 'string') ? handle.name : ''")
external fun wasmFileHandleName(handle: JsAny?): String

@JsFun("(fileName, create) => navigator.storage.getDirectory().then((root) => root.getFileHandle(fileName, { create }))")
external fun wasmOpfsGetFileHandle(fileName: String, create: Boolean): Promise<JsAny?>

@JsFun("(fileHandle) => fileHandle.getFile()")
external fun wasmGetFileFromHandle(fileHandle: JsAny): Promise<JsAny?>

@JsFun("(source) => (source && typeof source.getFile === 'function') ? source.getFile() : Promise.resolve(source)")
external fun wasmResolveImportFile(source: JsAny): Promise<JsAny?>

@JsFun("(file) => file.arrayBuffer()")
external fun wasmArrayBuffer(file: JsAny): Promise<JsAny?>

@JsFun("(fileHandle) => fileHandle.createWritable()")
external fun wasmCreateWritable(fileHandle: JsAny): Promise<JsAny?>

@JsFun("(writable, data) => writable.write(data)")
external fun wasmWritableWrite(writable: JsAny, data: JsAny): Promise<JsAny?>

@JsFun("(writable) => writable.close()")
external fun wasmWritableClose(writable: JsAny): Promise<JsAny?>

object WasmDatabaseTransferRegistry {
    private var generatedPathIndex: Int = 0
    private val exportTargetsByPath: MutableMap<String, JsAny> = mutableMapOf()
    private val importSourcesByPath: MutableMap<String, JsAny> = mutableMapOf()

    fun registerExportTarget(pathHint: String, fileHandle: JsAny): String {
        val normalizedPath = normalizePathHint(pathHint)
        exportTargetsByPath[normalizedPath] = fileHandle
        return normalizedPath
    }

    fun registerImportSource(pathHint: String, source: JsAny): String {
        val normalizedPath = normalizePathHint(pathHint)
        importSourcesByPath[normalizedPath] = source
        return normalizedPath
    }

    internal fun consumeExportTarget(path: String): JsAny? = exportTargetsByPath.remove(path)

    internal fun consumeImportSource(path: String): JsAny? = importSourcesByPath.remove(path)

    private fun normalizePathHint(pathHint: String): String {
        if (pathHint.isNotBlank()) return pathHint
        generatedPathIndex += 1
        return "backup-$generatedPathIndex.db"
    }
}
