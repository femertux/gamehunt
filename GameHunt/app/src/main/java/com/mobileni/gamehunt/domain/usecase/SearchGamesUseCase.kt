package com.mobileni.gamehunt.domain.usecase

import androidx.paging.PagingData
import com.mobileni.gamehunt.domain.model.Game
import com.mobileni.gamehunt.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case responsible for retrieving the list of game genres.
 *
 * Returns a Flow of Result containing a list of Genre objects.
 */
class SearchGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    /**
     * Executes the fetch operation from the repository.
     * Emits a Flow of Result wrapping the list of genres.
     */
    operator fun invoke(
        genreSlug: String? = null,
        search: String? = null
    ): Flow<PagingData<Game>> = repository.searchGames(genreSlug, search)
}