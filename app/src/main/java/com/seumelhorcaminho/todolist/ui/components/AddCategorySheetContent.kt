package com.seumelhorcaminho.todolist.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.seumelhorcaminho.todolist.R

@Composable
fun AddCategorySheetContent(
    categoryName: String,
    selectedEmoji: String,
    onCategoryNameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onEmojiClick: () -> Unit
) {
    val sheetBackgroundColor = colorResource(id = R.color.special_dark)

    Surface(
        color = sheetBackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            TextField(
                value = categoryName,
                onValueChange = onCategoryNameChange,
                label = { Text("Nome da categoria") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White.copy(alpha = 0.7f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onEmojiClick,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Emoji Icon",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = selectedEmoji)
                }

                Button(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    onClick = onSaveClick,
                    enabled = categoryName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = sheetBackgroundColor,
                        disabledContainerColor = colorResource(id = R.color.grey_70),
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "Salvar categoria")
                }
            }
        }
    }
}