package com.softartdev.notedelight.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FileRepoTest {
    private lateinit var fileRepo: FileRepo
    private lateinit var fileSystem: FakeFileSystem
    private val rootPath = "/test".toPath()

    @BeforeTest
    fun setUp() {
        fileSystem = FakeFileSystem()
        fileRepo = TestFileRepo(fileSystem, rootPath)
        
        // Create test directory structure
        fileSystem.createDirectories(rootPath)
        fileSystem.write(rootPath.resolve("file1.txt")) { writeUtf8("Content of file1") }
        fileSystem.write(rootPath.resolve("file2.txt")) { writeUtf8("Content of file2") }
        val subdir = rootPath.resolve("subdir")
        fileSystem.createDirectories(subdir)
        fileSystem.write(subdir.resolve("file3.txt")) { writeUtf8("Content of file3") }
    }

    @AfterTest
    fun tearDown() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun goToStartPath_shouldListRootDirectory() = runTest {
        // When
        fileRepo.goToStartPath()
        val result = fileRepo.fileListFlow.first()

        // Then
        assertTrue(result.isNotEmpty())
        assertContains(result, "📂$rootPath")
        assertContains(result, "🔙..")
        assertContains(result, "📄 file1.txt")
        assertContains(result, "📄 file2.txt")
        assertContains(result, "📁 subdir")
    }

    @Test
    fun goTo_shouldNavigateToSubdirectory() = runTest {
        // Given
        fileRepo.goToStartPath()
        fileRepo.fileListFlow.first() // Wait for initial load

        // When
        fileRepo.goTo("📁 subdir")
        val result = fileRepo.fileListFlow.first()

        // Then
        val subdirPath = rootPath.resolve("subdir")
        assertContains(result, "📂$subdirPath")
        assertContains(result, "🔙..")
        assertContains(result, "📄 file3.txt")
    }

    @Test
    fun goTo_shouldNavigateUpWithBackFolder() = runTest {
        // Given
        fileRepo.goToStartPath()
        fileRepo.fileListFlow.first()
        fileRepo.goTo("📁 subdir")
        fileRepo.fileListFlow.first()

        // When
        fileRepo.goTo("🔙..")
        val result = fileRepo.fileListFlow.first()

        // Then
        assertContains(result, "📂$rootPath")
        assertContains(result, "📄 file1.txt")
        assertContains(result, "📄 file2.txt")
    }

    @Test
    fun goTo_shouldReadFileContent() = runTest {
        // Given
        fileRepo.goToStartPath()
        fileRepo.fileListFlow.first()

        // When
        fileRepo.goTo("📄 file1.txt")
        val result = fileRepo.fileListFlow.first()

        // Then
        val filePath = rootPath.resolve("file1.txt")
        assertContains(result, "📂$filePath")
        assertContains(result, "🔙..")
        assertContains(result, "📖")
        assertContains(result, "Content of file1")
    }

    @Test
    fun goTo_shouldHandleFileContentClick() = runTest {
        // Given
        fileRepo.goToStartPath()
        fileRepo.fileListFlow.first()
        fileRepo.goTo("📄 file1.txt")
        fileRepo.fileListFlow.first()

        // When
        fileRepo.goTo("📖")
        val result = fileRepo.fileListFlow.first()

        // Then
        // Should append file content again
        val contentCount = result.count { it == "📖" }
        assertTrue(contentCount >= 2, "File content should be appended")
    }

    @Test
    fun goTo_shouldHandleNonExistentFile() = runTest {
        // Given
        fileRepo.goToStartPath()
        fileRepo.fileListFlow.first()

        // When
        fileRepo.goTo("📄 nonexistent.txt")
        val result = fileRepo.fileListFlow.first()

        // Then
        // Should not change the list (file not found)
        assertContains(result, "📂$rootPath")
    }

    @Test
    fun goToStartPath_shouldHandleInvalidPath() = runTest {
        // Given - create a separate FakeFileSystem for this test to avoid conflicts
        val invalidFileSystem = FakeFileSystem()
        val invalidRepo = TestFileRepo(invalidFileSystem, "/nonexistent".toPath())

        // When
        invalidRepo.goToStartPath()
        val result = invalidRepo.fileListFlow.first()

        // Then
        // When path doesn't exist, metadataOrNull returns null and goTo returns early
        // The flow should still contain the loading state since goTo returns early without updating
        assertTrue(result.isNotEmpty())
        // The initial loading state should remain since goTo returns early
        assertEquals(listOf("🔁loading..."), result)
        
        invalidFileSystem.checkNoOpenFiles()
    }

    @Test
    fun fileListFlow_shouldEmitUpdates() = runTest {
        // Given
        val initialValue = fileRepo.fileListFlow.first()
        assertEquals(listOf("🔁loading..."), initialValue)

        // When
        fileRepo.goToStartPath()
        val updatedValue = fileRepo.fileListFlow.first()

        // Then
        assertTrue(updatedValue.size > 1)
        assertContains(updatedValue, "📂$rootPath")
    }
}
