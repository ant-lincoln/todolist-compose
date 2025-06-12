package com.seumelhorcaminho.todolist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmojiPickerSheetContent(
    onEmojiSelected: (String) -> Unit
) {
    val emojis = listOf(
        "📚", "💼", "🛍️", "🩺", "🏅", "🎵", "✈️", "🏠", "💡", "💰", "🎉", "❤️",
        "⚙️", "📅", "📍", "🔑", "🖥️", "📞", "🔔", "🎁", "✉️", "📈", "📌"
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 48.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(emojis) { emoji ->
            Text(
                text = emoji,
                fontSize = 28.sp,
                modifier = Modifier.clickable { onEmojiSelected(emoji) }
            )
        }
    }
}