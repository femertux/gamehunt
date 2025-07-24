package com.mobileni.gamehunt.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mobileni.gamehunt.R
import com.mobileni.gamehunt.domain.model.Game
import com.mobileni.gamehunt.domain.model.Genre
import com.mobileni.gamehunt.presentation.components.FilteredGameCard
import com.mobileni.gamehunt.presentation.components.GenreChip
import com.mobileni.gamehunt.presentation.components.PopularGameCard
import com.mobileni.gamehunt.presentation.components.SearchBar
import com.mobileni.gamehunt.presentation.ui.theme.darkBlue
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeRoute(
    onNavigateToGameDetail: (slug: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Observes UI state from ViewModel
    val genresUiState by viewModel.genresUiState.collectAsState()
    val gamesUiState by viewModel.gamesUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredGames = viewModel.filteredGamesPaging.collectAsLazyPagingItems()

    // Collects navigation effects to navigate to game detail screen
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetail -> {
                    onNavigateToGameDetail(effect.slug)
                }
            }
        }
    }

    // Displays the main home UI
    HomeScreen(
        genres = genresUiState.genres,
        selectedGenreId = genresUiState.selectedGenreId,
        onGenreSelected = { viewModel.onEvent(HomeEvent.GenreSelected(it.slug)) },
        onGameClick = { viewModel.onEvent(HomeEvent.GameSelected(it)) },
        onRetry = { viewModel.onEvent(HomeEvent.Retry) },
        searchQuery = searchQuery,
        onSearchQueryChanged = { viewModel.onEvent(HomeEvent.SearchChanged(it)) },
        popularGames = gamesUiState.popularGames,
        filteredGames = filteredGames
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    genres: List<Genre>,
    selectedGenreId: Int?,
    onGenreSelected: (Genre) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onGameClick: (String) -> Unit,
    onRetry: () -> Unit,
    popularGames: List<Game>,
    filteredGames: LazyPagingItems<Game>
) {
    // Collapsing toolbar behavior when scrolling
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val listState = rememberLazyListState()

    // Changes title based on scroll position
    val titleText = if (collapsedFraction < 0.5f) stringResource(R.string.welcome_back_gamer) else stringResource(R.string.app_name)

    // Filters and UI flags
    val isFiltering = searchQuery.length > 3 || selectedGenreId != null
    val isLoading = filteredGames.loadState.refresh is LoadState.Loading
    val isEmpty = filteredGames.itemCount == 0 && !isLoading
    val pagingError = (filteredGames.loadState.refresh as? LoadState.Error)?.error?.message

    Scaffold(
        topBar = {
            // Top bar with scroll-aware title
            LargeTopAppBar(
                title = {
                    Text(
                        text = titleText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = darkBlue,
                    scrolledContainerColor = darkBlue,
                ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = darkBlue
    ) { innerPadding ->
        // Main scrollable list of content
        LazyColumn(
            state = listState,
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search input
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChanged = onSearchQueryChanged,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Genre chips
            item {
                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres) { genre ->
                        GenreChip(
                            text = genre.name,
                            isSelected = genre.id == selectedGenreId,
                            onClick = { onGenreSelected(genre) }
                        )
                    }
                }
            }

            // Popular games section (shown if not filtering)
            if (!isFiltering && popularGames.isNotEmpty()) {
                item {
                    PopularGamesSection(
                        games = popularGames,
                        onGameClick = { onGameClick(it.slug) },
                    )
                }
            }

            // Loading indicator
            if (isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 148.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }

            // Empty result message
            if (isEmpty && pagingError.isNullOrBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.no_games_found),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Error message with retry option
            if (!pagingError.isNullOrBlank()) {
                item {
                    ErrorMessage(
                        message = stringResource(R.string.we_couldn_t_load_the_games_please_check_your_connection_and_try_again),
                        onRetry = onRetry
                    )
                }
            }

            // Games list (paginated)
            if (!isLoading) {
                if (filteredGames.itemCount > 0) {
                    item {
                        Text(
                            text = if (isFiltering) stringResource(R.string.results) else stringResource(
                                R.string.all_games
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(start = 16.dp, top = 24.dp)
                        )
                    }
                }
                items(filteredGames.itemCount) { index ->
                    filteredGames[index]?.let { game ->
                        FilteredGameCard(
                            game = game,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onGameClick = { onGameClick(game.slug) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PopularGamesSection(
    games: List<Game>,
    modifier: Modifier = Modifier,
    onGameClick: (Game) -> Unit = {}
) {
    Column(modifier = modifier) {
        // Section title
        Text(
            text = stringResource(R.string.popular_games),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )

        // Horizontal list of popular games
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(games) { game ->
                PopularGameCard(
                    game = game,
                    modifier = Modifier
                        .clickable { onGameClick(game) }
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 64.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        androidx.compose.material3.Button(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    val dummyGenres = listOf(
        Genre(1, "Action", "action", ""),
        Genre(2, "Adventure", "action", ""),
        Genre(3, "RPG", "action", "")
    )

    val dummyGames = List(5) {
        Game(
            id = it,
            name = "Game $it",
            slug = "",
            imageUrl = "https://media.rawg.io/media/games/20a/20aa03a10cda45239fe22d035c0ebe64.jpg",
            rating = 4.5f,
            releaseDate = "2024-01-01",
            genres = listOf("Action", "Adventure"),
        )
    }

    var selectedId by remember { mutableStateOf<Int?>(1) }
    var query by remember { mutableStateOf("") }

    val fakePagingItems = remember {
        flowOf(PagingData.from(dummyGames))
    }.collectAsLazyPagingItems()

    HomeScreen(
        genres = dummyGenres,
        selectedGenreId = selectedId,
        onGenreSelected = { selectedId = it.id },
        onGameClick = {},
        onRetry = {},
        searchQuery = query,
        onSearchQueryChanged = { query = it },
        popularGames = dummyGames,
        filteredGames = fakePagingItems
    )
}