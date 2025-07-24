package com.mobileni.gamehunt.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mobileni.gamehunt.data.mapper.toDomain
import com.mobileni.gamehunt.data.network.RawgApiService
import com.mobileni.gamehunt.domain.model.Game

/**
 * PagingSource implementation for loading paginated game data from the RAWG API.
 *
 * Supports filtering by genre slug and/or search query. This class is used in conjunction
 * with the Paging library to provide infinite scrolling in the UI.
 */
class GamesPagingSource(
    private val api: RawgApiService,
    private val apiKey: String,
    private val genreSlug: String?,
    private val query: String?
) : PagingSource<Int, Game>() {

    /**
     * Loads a page of data from the API based on the current page index.
     * Maps the API response to domain models and handles pagination keys.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val page = params.key ?: 1
        return try {
            val response = api.getGames(
                apiKey = apiKey,
                genreSlug = genreSlug.takeIf { it?.isNotBlank() == true },
                search = query.takeIf { it?.isNotBlank() == true },
                page = page,
                pageSize = params.loadSize
            )
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.results.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * Calculates the key to be passed to load() when refreshing data.
     * Uses the anchor position to determine the closest page.
     */
    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}