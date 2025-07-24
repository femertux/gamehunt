package com.mobileni.gamehunt.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GenreChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        ),
        shape =  RoundedCornerShape(50),
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, name = "GenreChip Preview")
@Composable
fun GenreChipPreview() {
    var selected by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        GenreChip(
            text = "Action",
            isSelected = selected,
            onClick = { selected = !selected }
        )
    }
}