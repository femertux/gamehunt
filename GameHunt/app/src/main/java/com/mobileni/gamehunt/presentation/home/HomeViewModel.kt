package com.mobileni.gamehunt.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobileni.gamehunt.domain.di.IoDispatcher
import com.mobileni.gamehunt.domain.model.Game
import com.mobileni.gamehunt.domain.model.Genre
import com.mobileni.gamehunt.domain.usecase.GetGenresUseCase
import com.mobileni.gamehunt.domain.usecase.GetPopularGamesUseCase
import com.mobileni.gamehunt.domain.usecase.SearchGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val getGenresUseCase: GetGenresUseCase,
    private val getPopularGamesUseCase: GetPopularGamesUseCase,
    private val searchGamesUseCase: SearchGamesUseCase
) : ViewModel() {

    // Holds the state for genre filters (list, selected genre, loading and error)
    private val _genresUiState = MutableStateFlow(GenresUiState())
    val genresUiState: StateFlow<GenresUiState> = _genresUiState

    // Holds the state for the popular games section
    private val _gamesUiState = MutableStateFlow(GamesUiState())
    val gamesUiState: StateFlow<GamesUiState> = _gamesUiState

    // Current search query entered by user
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Paging data stream for the filtered games (search and/or genre based)
    private val _filteredGamesPaging = MutableStateFlow<PagingData<Game>>(PagingData.empty())
    val filteredGamesPaging: StateFlow<PagingData<Game>> = _filteredGamesPaging

    // SharedFlow for one-time side effects
    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        // Load all required data when ViewModel starts
        getGenres()
        getPopularGames()
        loadFilteredGames()
    }

    // Reload all sections (used after error/retry)
    private fun reloadAll() {
        getGenres()
        getPopularGames()
        loadFilteredGames()
    }

    // Handle UI events
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SearchChanged -> {
                _searchQuery.value = event.query
                if (event.query.length > 3 || event.query.isEmpty()) {
                    loadFilteredGames()
                }
            }

            is HomeEvent.GenreSelected -> {
                val selectedGenre = _genresUiState.value.genres.find { it.slug == event.genreSlug }
                val wasAlreadySelected = selectedGenre?.id == _genresUiState.value.selectedGenreId
                _genresUiState.update {
                    it.copy(selectedGenreId = if (wasAlreadySelected) null else selectedGenre?.id)
                }
                loadFilteredGames()
            }

            is HomeEvent.Retry -> {
                reloadAll()
            }

            is HomeEvent.GameSelected -> {
                // Trigger navigation to detail screen
                viewModelScope.launch {
                    _effect.emit(HomeEffect.NavigateToDetail(event.slug))
                }
            }
        }
    }

    // Fetches genre list and updates UI state
    private fun getGenres() {
        getGenresUseCase()
            .onStart {
                _genresUiState.update {
                    it.copy(isLoading = true)
                }
            }.map { result ->
                result.fold(
                    onSuccess = { genres ->
                        _genresUiState.update {
                            it.copy(
                                genres = genres,
                                isLoading = false,
                                error = "",
                            )
                        }
                    },
                    onFailure = { exception ->
                        _genresUiState.update {
                            it.copy(
                                error = exception.message.orEmpty(),
                                isLoading = false,
                            )
                        }
                    },
                )
            }.flowOn(coroutineDispatcher)
            .launchIn(viewModelScope)
    }

    // Fetches most popular games
    private fun getPopularGames() {
        getPopularGamesUseCase()
            .onStart {
                _gamesUiState.update { it.copy(isLoading = true) }
            }
            .map { result ->
                result.fold(
                    onSuccess = { games ->
                        _gamesUiState.update {
                            it.copy(
                                popularGames = games,
                                isLoading = false,
                                error = ""
                            )
                        }
                    },
                    onFailure = { exception ->
                        _gamesUiState.update {
                            it.copy(
                                error = exception.message.orEmpty(),
                                isLoading = false
                            )
                        }
                    }
                )
            }
            .flowOn(coroutineDispatcher)
            .launchIn(viewModelScope)
    }

    // Loads paged list of games based on genre and/or search query
    private fun loadFilteredGames() {
        searchGamesUseCase(
            genreSlug = _genresUiState.value.genres.find { it.id == _genresUiState.value.selectedGenreId }?.slug,
            search = _searchQuery.value
        )
            .cachedIn(viewModelScope)
            .onEach { pagingData ->
                _filteredGamesPaging.value = pagingData
            }
            .launchIn(viewModelScope)
    }
}

// Represents the UI state for genres section
data class GenresUiState(
    val genres: List<Genre> = emptyList(),
    val selectedGenreId: Int? = null,
    val isLoading: Boolean = false,
    val error: String = "",
)

// Represents the UI state for popular games section
data class GamesUiState(
    val popularGames: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)

// Defines user actions from HomeScreen
sealed class HomeEvent {
    data class SearchChanged(val query: String) : HomeEvent()
    data class GenreSelected(val genreSlug: String?) : HomeEvent()
    object Retry : HomeEvent()
    data class GameSelected(val slug: String) : HomeEvent()
}

// Defines one-time effects emitted by ViewModel
sealed class HomeEffect {
    data class NavigateToDetail(val slug: String) : HomeEffect()
}