package com.mobileni.gamehunt.presentation.detail

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobileni.gamehunt.R
import com.mobileni.gamehunt.domain.model.GameDetail
import com.mobileni.gamehunt.domain.model.Genre
import com.mobileni.gamehunt.domain.model.Screenshot
import com.mobileni.gamehunt.presentation.home.ErrorMessage
import com.mobileni.gamehunt.presentation.ui.theme.darkBlue
import com.mobileni.gamehunt.presentation.ui.theme.yellow

@Composable
fun GameDetailRoute(
    slug: String,
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = hiltViewModel()
) {
    // Collects game detail and screenshots from the ViewModel
    val state by viewModel.gameDetailState.collectAsState()
    val screenshotState by viewModel.screenshotState.collectAsState()
    val context = LocalContext.current

    // Handles share intent effect from ViewModel
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GameDetailEffect.ShareIntent -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, effect.title)
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, context.getString(R.string.share_via))
                    )
                }
            }
        }
    }

    // Loads game detail on initial composition
    LaunchedEffect(slug) {
        viewModel.onEvent(GameDetailEvent.Load(slug))
    }

    // Renders the detail screen UI
    GameDetailScreen(
        uiState = state,
        screenshotState = screenshotState,
        onBack = onBack,
        onRetry = { viewModel.onEvent(GameDetailEvent.Retry) },
        onShare = { name, website ->
            viewModel.onEvent(GameDetailEvent.ShareGame(name, website))
        }
    )
}

@Composable
fun GameDetailScreen(
    uiState: GameDetailUiState,
    screenshotState: ScreenshotUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onShare: (name: String, website: String) -> Unit
) {
    // UI state to toggle description expansion and screenshot preview
    var isExpanded by remember { mutableStateOf(false) }
    var selectedScreenshot by remember { mutableStateOf<String?>(null) }

    when {
        // Shows loading spinner
        uiState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(darkBlue),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Shows error screen if data could not be loaded
        uiState.error.isNotEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(
                    message = stringResource(R.string.oops_we_couldn_t_load_the_game_details),
                    onRetry = onRetry
                )
            }
        }

        // Main content
        uiState.detail != null -> {
            val game = uiState.detail

            // Converts dominantColor (hex string) to Color, fallback to black
            val dominantColor = runCatching {
                Color(game.dominantColor.toColorInt())
            }.getOrDefault(Color.Black)

            // Tracks scroll for potential future effects
            val scrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dominantColor)
            ) {

                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Banner image with gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                    ) {
                        // Game background
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(game.backgroundImage)
                                .crossfade(800)
                                .build(),
                            contentDescription = game.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, dominantColor),
                                        startY = 0f,
                                        endY = 1000f
                                    )
                                )
                        )

                        // Title and rating badge
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = game.name,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )

                                Box(
                                    modifier = Modifier
                                        .background(
                                            yellow.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = yellow,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "${game.rating}",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = game.genres.joinToString(" | ") { it.name },
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                lineHeight = 16.sp,
                            )
                        }
                    }

                    // Game description (expandable)
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = stringResource(R.string.about_game),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))

                        val aboutText = HtmlCompat.fromHtml(
                            game.description,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        ).toString()

                        Text(
                            text = aboutText,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.animateContentSize()
                        )

                        // See more / See less toggle
                        if (aboutText.length > 100) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (isExpanded) stringResource(R.string.see_less) else stringResource(
                                    R.string.see_more
                                ),
                                color = MaterialTheme.colorScheme.primary ,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable { isExpanded = !isExpanded }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    // Screenshots grid
                    if (screenshotState.screenshots.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.screenshots),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 600.dp)
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            contentPadding = PaddingValues(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(screenshotState.screenshots) { screenshot ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(screenshot.imageUrl)
                                        .crossfade(800)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedScreenshot = screenshot.imageUrl
                                        }
                                )
                            }
                        }

                        Spacer(Modifier.height(60.dp))
                    }
                }

                // Back button (top-left)
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(top = 20.dp, start = 4.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Share button (top-right, if website is present)
                if (game.website.isNotEmpty()) {
                    IconButton(
                        onClick = { onShare(game.name, game.website) },
                        modifier = Modifier
                            .padding(top = 20.dp, end = 4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = Color.White
                        )
                    }
                }

                // Screenshot full preview dialog
                if (selectedScreenshot != null) {
                    Dialog(onDismissRequest = { selectedScreenshot = null }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f)
                                .background(Color.Black, shape = RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            AsyncImage(
                                model = selectedScreenshot,
                                contentDescription = "Full screenshot",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            IconButton(
                                onClick = { selectedScreenshot = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGameDetailScreen() {
    val dummyGame = GameDetail(
        id = 1,
        slug = "call-of-duty",
        name = "Call of Duty: Going Dark",
        description = "Official CALL OF DUTY® designed exclusively for mobile phones. Play iconic multiplayer maps and modes anytime, anywhere.",
        backgroundImage = "https://media.rawg.io/media/games/623/623dae1c68e6b6fc46d72c44d3f1c0f5.jpg",
        rating = 4.7,
        dominantColor = "#080E1F",
        genres = listOf(
            Genre(id = 1, name = "Action", slug = "action", imageUrl = ""),
            Genre(id = 2, name = "Shooter", slug = "shooter", imageUrl = ""),
            Genre(id = 3, name = "PVP", slug = "pvp", imageUrl = "")
        ),
        website = "http://www.rockstargames.com/reddeadredemption/"
    )

    val dummyScreenshots = listOf(
        Screenshot(
            id = 1,
            imageUrl = "https://media.rawg.io/media/screenshots/1ac/1ac19f31974314855ad7be266adeb500.jpg"
        ),
        Screenshot(
            id = 2,
            imageUrl = "https://media.rawg.io/media/screenshots/6a0/6a08afca95261a2fe221ea9e01d28762.jpg"
        ),
        Screenshot(
            id = 3,
            imageUrl = "https://media.rawg.io/media/screenshots/cdd/cdd31b6b4a687425a87b5ce231ac89d7.jpg"
        )
    )

    GameDetailScreen(
        uiState = GameDetailUiState(
            isLoading = false,
            error = "",
            detail = dummyGame
        ),
        screenshotState = ScreenshotUiState(
            isLoading = false,
            screenshots = dummyScreenshots,
            error = ""
        ),
        onBack = {},
        onRetry = {},
        onShare = {_, _ ->}
    )
}

