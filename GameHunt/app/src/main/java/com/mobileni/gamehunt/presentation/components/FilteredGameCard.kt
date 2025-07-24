package com.mobileni.gamehunt.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobileni.gamehunt.domain.model.Game
import com.mobileni.gamehunt.presentation.ui.theme.lightGray
import com.mobileni.gamehunt.presentation.ui.theme.yellow
import com.mobileni.gamehunt.utils.formatReleaseDate

@Composable
fun FilteredGameCard(
    modifier: Modifier = Modifier,
    game: Game,
    onGameClick: (Game) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(lightGray)
            .padding(12.dp)
            .clickable {
                onGameClick(game)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(game.imageUrl)
                .crossfade(600)
                .build(),
            contentDescription = game.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 100.dp, height = 70.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = game.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = game.releaseDate?.let { formatReleaseDate(it) }.orEmpty(),
                color = Color.Gray,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 1.dp, bottom = 1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = yellow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(game.rating),
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 14.sp
                )
            }

            Text(
                text = game.genres.take(2).joinToString(" • "),
                color = Color.LightGray,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewFilteredGameCard() {
    val dummyGame = Game(
        id = 1,
        name = "Hades II",
        slug = "",
        imageUrl = "https://media.rawg.io/media/games/1f2/1f2269704c37a3f6ecdc1e2bc43fe272.jpg",
        rating = 4.6f,
        releaseDate = "2024-04-19",
        genres = listOf("Roguelike", "Indie", "Action")
    )

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        FilteredGameCard(game = dummyGame){}
    }
}