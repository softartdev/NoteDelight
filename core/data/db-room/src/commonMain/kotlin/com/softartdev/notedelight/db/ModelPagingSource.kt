package com.softartdev.notedelight.db

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.db.Note as NoteDBO

internal class ModelPagingSource(
    private val delegate: PagingSource<Int, NoteDBO>,
) : PagingSource<Int, Note>() {

    init {
        delegate.registerInvalidatedCallback(::invalidate)
    }

    override val jumpingSupported: Boolean
        get() = delegate.jumpingSupported

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> =
        when (val result = delegate.load(params)) {
            is LoadResult.Page -> LoadResult.Page(
                data = result.data.map(NoteDBO::model),
                prevKey = result.prevKey,
                nextKey = result.nextKey,
                itemsBefore = result.itemsBefore,
                itemsAfter = result.itemsAfter,
            )
            is LoadResult.Error -> LoadResult.Error(result.throwable)
            is LoadResult.Invalid -> LoadResult.Invalid()
        }

    override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }
}
