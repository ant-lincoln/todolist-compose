package com.seumelhorcaminho.todolist.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seumelhorcaminho.todolist.ui.theme.TodoListTheme

@Composable
fun AddCategorySheetContent(
    onSaveClick: (name: String, emoji: String) -> Unit,
    onEmojiClick: () -> Unit
) {
    var categoryName by rememberSaveable { mutableStateOf("") }
    var selectedEmoji by rememberSaveable { mutableStateOf("⭐") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 16.dp)
    ) {
        TextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Nome da categoria") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
                disabledContainerColor = Color.DarkGray.copy(alpha = 0.3f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //Button emoji
            Button(
                onClick = onEmojiClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text(text = "$selectedEmoji Emoji")
            }

            //Button save
            Button(
                onClick = { onSaveClick(categoryName, selectedEmoji) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color.DarkGray),
                enabled = categoryName.isNotBlank()
            ) {
                Text(
                    text = "Salvar categoria",
                    color = Color.DarkGray
                )

            }
        }
    }

}

@Preview
@Composable
private fun AddCategorySheetContentPreview() {
    TodoListTheme(darkTheme = true) {
        AddCategorySheetContent(
            onSaveClick = { _, _ -> },
            onEmojiClick = {}
        )
    }
}
