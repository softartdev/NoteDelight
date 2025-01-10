package com.softartdev.notedelight.db

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.model.Note as NoteModel

class ModelPagingSource(
    private val delegate: PagingSource<Int, Note>
) : PagingSource<Int, NoteModel>() {

    override fun getRefreshKey(state: PagingState<Int, NoteModel>): Int? {
        val pagingState: PagingState<Int, Note> = PagingState(
            pages = state.pages.map { page: LoadResult.Page<Int, NoteModel> ->
                val pageResult: LoadResult.Page<Int, Note> = LoadResult.Page(
                    data = page.data.map(NoteModel::dbo),
                    prevKey = page.prevKey,
                    nextKey = page.nextKey,
                    itemsBefore = page.itemsBefore,
                    itemsAfter = page.itemsAfter,
                )
                return@map pageResult
            },
            anchorPosition = state.anchorPosition,
            config = state.config,
            leadingPlaceholderCount = 0,
        )
        return delegate.getRefreshKey(pagingState)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NoteModel> =
        when (val loadResult: LoadResult<Int, Note> = delegate.load(params)) {
            is LoadResult.Page -> LoadResult.Page(
                data = loadResult.data.toModel(),
                prevKey = loadResult.prevKey,
                nextKey = loadResult.nextKey,
                itemsBefore = loadResult.itemsBefore,
                itemsAfter = loadResult.itemsAfter,
            )
            is LoadResult.Invalid -> LoadResult.Invalid()
            is LoadResult.Error -> LoadResult.Error(loadResult.throwable)
        }
}