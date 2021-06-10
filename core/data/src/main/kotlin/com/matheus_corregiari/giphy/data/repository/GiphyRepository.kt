package com.matheus_corregiari.giphy.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.matheus_corregiari.giphy.data.local.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.entity.Favorite
import com.matheus_corregiari.giphy.data.model.GiphyItemDTO
import com.matheus_corregiari.giphy.data.remote.ApiProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GiphyRepository internal constructor() {

    private val pagingConfig = PagingConfig(
        pageSize = 40,
        enablePlaceholders = true,
        initialLoadSize = 400
    )

    fun trendingGiphys(onlyFavored: Boolean): Pager<Int, GiphyItemDTO> {
        return Pager(pagingConfig, initialKey = 1) {
            SearchPagingSource(
                null,
                onlyFavored
            )
        }
    }

    fun searchGiphys(term: String?): Pager<Int, GiphyItemDTO> {
        return Pager(pagingConfig, initialKey = 1) {
            SearchPagingSource(
                term ?: "",
                false
            )
        }
    }

    fun favoriteGiphy(giphy: GiphyItemDTO) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                giphy.favorite = giphy.favorite.not()
                val hasOnList = DatabaseProvider.favoriteDao.get(giphy.id) != null

                if (giphy.favorite.not() && hasOnList) {
                    DatabaseProvider.favoriteDao.delete(giphy.asFavorite())
                }
                if (giphy.favorite && hasOnList.not()) {
                    DatabaseProvider.favoriteDao.insert(giphy.asFavorite())
                }
            }
        }
    }
}

private class SearchPagingSource(
    private val query: String?,
    private val onlyFavored: Boolean
) : PagingSource<Int, GiphyItemDTO>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GiphyItemDTO> {
        return kotlin.runCatching {
            if (onlyFavored) {
                return@runCatching LoadResult.Page<Int, GiphyItemDTO>(
                    data = DatabaseProvider.favoriteDao.getAll().map(Favorite::asGiphyItemDTO),
                    prevKey = null,
                    nextKey = null
                )
            }

            val nextPageNumber = params.key ?: 1
            val response = if (query.isNullOrBlank().not()) {
                ApiProvider.api.searchGifs(params.loadSize, nextPageNumber, query ?: "")
            } else {
                ApiProvider.api.fetchTrendingGifs(params.loadSize, nextPageNumber)
            }.gifList

            response.onEach { item ->
                item.favorite = DatabaseProvider.favoriteDao.get(item.id) != null
            }
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.isNotEmpty()) nextPageNumber + 1 else null
            )
        }.getOrElse { LoadResult.Error(it) }
    }

    override fun getRefreshKey(state: PagingState<Int, GiphyItemDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}