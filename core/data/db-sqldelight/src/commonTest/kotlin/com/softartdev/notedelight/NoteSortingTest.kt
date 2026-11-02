package com.softartdev.notedelight

import androidx.paging.PagingSource
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.model.NoteSort
import com.softartdev.notedelight.model.SortField
import com.softartdev.notedelight.model.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.datetime.LocalDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class NoteSortingTest : BaseTest() {

    private var noteDAO: NoteDAO? = null
    private val mutex = Mutex()

    private val bananaNote = Note(
        id = 1,
        title = "banana",
        text = "first created, middle modified",
        dateCreated = LocalDateTime(1998, 1, 1, 10, 0),
        dateModified = LocalDateTime(2024, 2, 2, 10, 0),
    )
    private val appleNote = Note(
        id = 2,
        title = "apple",
        text = "second created, latest modified",
        dateCreated = LocalDateTime(2024, 1, 2, 10, 0),
        dateModified = LocalDateTime(2024, 2, 3, 10, 0),
    )
    private val cherryNote = Note(
        id = 3,
        title = "Cherry",
        text = "latest created, earliest modified",
        dateCreated = LocalDateTime(2024, 1, 3, 10, 0),
        dateModified = LocalDateTime(1998, 2, 1, 10, 0),
    )

    @BeforeTest
    fun setUp() = runTest {
        Logger.setLogWriters(PrintLogWriter())
        deleteDb()
        noteDB()
        val dao = safeRepo.noteDAO
        noteDAO = dao
        dao.insert(bananaNote)
        dao.insert(appleNote)
        dao.insert(cherryNote)
    }

    @AfterTest
    fun tearDown() = runTest {
        NoteDAO.noteSortFlow.value = NoteSort()
        noteDAO?.deleteAll()
        safeRepo.closeDatabase()
        deleteDb()
        noteDAO = null
        Logger.setLogWriters()
    }

    @Test
    fun sortByModifiedDateDescAndAsc() = runTest {
        NoteDAO.noteSortFlow.value = NoteSort(field = SortField.DATE_MODIFIED, order = SortOrder.DESC)
        assertEquals(listOf("apple", "banana", "Cherry"), loadPagedTitles())

        NoteDAO.noteSortFlow.value = NoteSort(field = SortField.DATE_MODIFIED, order = SortOrder.ASC)
        assertEquals(listOf("Cherry", "banana", "apple"), loadPagedTitles())
    }

    @Test
    fun sortByCreatedDateDescAndAsc() = runTest {
        NoteDAO.noteSortFlow.value = NoteSort(field = SortField.DATE_CREATED, order = SortOrder.DESC)
        assertEquals(listOf("Cherry", "apple", "banana"), loadPagedTitles())

        NoteDAO.noteSortFlow.value = NoteSort(field = SortField.DATE_CREATED, order = SortOrder.ASC)
        assertEquals(listOf("banana", "apple", "Cherry"), loadPagedTitles())
    }

    @Test
    fun sortByTitleCaseInsensitiveAscAndDesc() = runTest {
        NoteDAO.noteSortFlow.value = NoteSort(field = SortField.TITLE, order = SortOrder.ASC)
        assertEquals(listOf("apple", "banana", "Cherry"), loadPagedTitles())

        NoteDAO.noteSortFlow.value = NoteSort(field = SortField.TITLE, order = SortOrder.DESC)
        assertEquals(listOf("Cherry", "banana", "apple"), loadPagedTitles())
    }

    @Test
    fun pagingDataFlowEmitsNewPagingDataOnSortChange() = runTest {
        val dao = requireNotNull(noteDAO)
        val firstPagingData = dao.pagingDataFlow.first()
        NoteDAO.noteSortFlow.value = NoteSort(field = SortField.TITLE, order = SortOrder.ASC)
        val secondPagingData = dao.pagingDataFlow.first()
        assertNotSame(firstPagingData, secondPagingData)
    }

    private suspend fun loadPagedTitles(): List<String> {
        val result = requireNotNull(noteDAO).pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            )
        )
        assertIs<PagingSource.LoadResult.Page<Int, Note>>(result)
        return result.data.map(Note::title)
    }

    private fun runTest(
        context: CoroutineContext = EmptyCoroutineContext,
        timeout: Duration = 60.seconds,
        testBody: suspend TestScope.() -> Unit,
    ): TestResult = kotlinx.coroutines.test.runTest(context, timeout) {
        mutex.withLock { testBody() }
    }
}
