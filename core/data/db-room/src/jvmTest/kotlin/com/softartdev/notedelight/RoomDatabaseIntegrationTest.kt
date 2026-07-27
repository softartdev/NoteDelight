package com.softartdev.notedelight

import androidx.paging.PagingSource
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.repository.JvmSafeRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import java.io.File
import java.nio.file.Files
import java.sql.DriverManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

class RoomDatabaseIntegrationTest {
    private lateinit var databaseFile: File
    private lateinit var safeRepo: JvmSafeRepo

    @BeforeTest
    fun setUp() {
        databaseFile = Files.createTempDirectory("notedelight-room3-")
            .resolve("notes.db")
            .toFile()
        safeRepo = JvmSafeRepo().apply { overrideDbPath(databaseFile.absolutePath) }
    }

    @AfterTest
    fun tearDown() = runTest {
        safeRepo.closeDatabase()
        databaseFile.delete()
        File("${databaseFile.absolutePath}-wal").delete()
        File("${databaseFile.absolutePath}-shm").delete()
        databaseFile.parentFile.delete()
    }

    @Test
    fun crudFlowPagingRawSqlAndReopen() = runTest {
        safeRepo.buildDbIfNeed()
        val first = note(id = 0, title = "first")
        val second = note(id = 0, title = "second", minute = 2)

        safeRepo.noteDAO.insert(first)
        safeRepo.noteDAO.insert(second)
        assertEquals(2, safeRepo.noteDAO.count())
        assertEquals(listOf("second", "first"), safeRepo.noteDAO.listFlow.first().map(Note::title))

        val page = safeRepo.noteDAO.pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )
        assertIs<PagingSource.LoadResult.Page<Int, Note>>(page)
        assertEquals(listOf("second", "first"), page.data.map(Note::title))
        assertEquals("2", safeRepo.execute("SELECT count(*) FROM note"))
        assertEquals(null, safeRepo.execute("UPDATE note SET title = 'raw SQL' WHERE id = 1"))
        assertEquals("raw SQL", safeRepo.noteDAO.load(1).title)

        val loaded = safeRepo.noteDAO.load(1)
        safeRepo.noteDAO.update(loaded.copy(title = "updated"))
        assertEquals("updated", safeRepo.noteDAO.load(1).title)
        safeRepo.noteDAO.delete(2)
        assertEquals(1, safeRepo.noteDAO.count())

        safeRepo.closeDatabase()
        safeRepo.buildDbIfNeed()
        assertEquals("updated", safeRepo.noteDAO.load(1).title)
        assertEquals("2", safeRepo.execute("PRAGMA user_version"))
    }

    @Test
    fun opensAndMigratesTheSharedV1Schema() = runTest {
        createRawV1Database(databaseFile)

        safeRepo.buildDbIfNeed()
        assertEquals("raw", safeRepo.noteDAO.load(41).title)
        safeRepo.noteDAO.insert(note(id = 42, title = "room"))
        safeRepo.closeDatabase()

        DriverManager.getConnection("jdbc:sqlite:${databaseFile.absolutePath}").use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT title FROM note WHERE id = 42").use { result ->
                    assertEquals(true, result.next())
                    assertEquals("room", result.getString(1))
                }
                statement.executeQuery("PRAGMA user_version").use { result ->
                    assertEquals(true, result.next())
                    assertEquals(2, result.getInt(1))
                }
            }
        }
    }

    @Test
    fun encryptionWrongPasswordAndRekey() = runTest {
        safeRepo.buildDbIfNeed()
        val noteDAO = safeRepo.noteDAO
        noteDAO.insert(note(id = 1, title = "secret"))
        safeRepo.encrypt("first password")
        assertEquals("secret", noteDAO.load(1).title)
        safeRepo.closeDatabase()

        val wrongPasswordRepo = JvmSafeRepo().apply {
            overrideDbPath(databaseFile.absolutePath)
        }
        wrongPasswordRepo.buildDbIfNeed("wrong password")
        assertFails { wrongPasswordRepo.noteDAO.count() }
        wrongPasswordRepo.closeDatabase()

        safeRepo.buildDbIfNeed("first password")
        assertEquals("secret", noteDAO.load(1).title)
        safeRepo.rekey("first password", "second password")
        assertEquals("secret", noteDAO.load(1).title)
        safeRepo.closeDatabase()
        safeRepo.buildDbIfNeed("second password")
        assertEquals("secret", noteDAO.load(1).title)

        safeRepo.decrypt("second password")
        assertEquals("secret", noteDAO.load(1).title)
    }

    private fun note(id: Long, title: String, minute: Int = 1): Note {
        val date = LocalDateTime(2026, 1, 2, 3, minute)
        return Note(
            id = id,
            title = title,
            text = "$title text",
            dateCreated = date,
            dateModified = date,
        )
    }

    private fun createRawV1Database(file: File) {
        DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}").use { connection ->
            connection.createStatement().use { statement ->
                statement.execute(
                    """
                    CREATE TABLE note (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        text TEXT NOT NULL,
                        dateCreated INTEGER NOT NULL,
                        dateModified INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                statement.execute(
                    "INSERT INTO note VALUES (41, 'raw', 'raw text', 1767322860000, 1767322860000)",
                )
                statement.execute("PRAGMA user_version = 1")
            }
        }
    }
}
