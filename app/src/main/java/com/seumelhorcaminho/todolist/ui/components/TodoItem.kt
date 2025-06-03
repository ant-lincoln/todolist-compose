package com.seumelhorcaminho.todolist.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.seumelhorcaminho.todolist.domain.Todo
import com.seumelhorcaminho.todolist.domain.todo1
import com.seumelhorcaminho.todolist.domain.todo2
import com.seumelhorcaminho.todolist.ui.theme.TodoListTheme

@Composable
fun TodoItem(
    todo: Todo,
    onCompleteChange: (Boolean) -> Unit = {},
    onItemClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val showDialog = remember { mutableStateOf(false) }

    // Funções para confirmar ou cancelar a exclusão
    fun onDeleteConfirmed() {
        onDeleteClick()
        showDialog.value = false
    }

    fun onDeleteCanceled() {
        showDialog.value = false
    }

    // Layout principal do item de todo
    Surface(
        onClick = onItemClick,
        modifier = modifier.padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularCheckbox(
                checked = todo.isCompleted,
                onCheckedChange = onCompleteChange
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (todo.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = todo.description.let {
                        if (it.isNullOrBlank()) "\uD83C\uDF43" else it
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { showDialog.value = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir tarefa",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Confirmar exclusão") },
            text = { Text("Você tem certeza que deseja excluir esta tarefa?") },
            confirmButton = {
                Button(onClick = { onDeleteConfirmed() }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { onDeleteCanceled() }) {
                    Text("Cancelar")
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}

@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val borderColor = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Preview
@Composable
private fun TodoItemModernPreview() {
    TodoListTheme {
        Column {
            TodoItem(todo = todo1)
            TodoItem(todo = todo2)
        }
    }
}

@Preview
@Composable
private fun TodoItemCompletedPreview() {
    TodoListTheme {
        Column {
            TodoItem(todo = todo1.copy(isCompleted = true))
            TodoItem(todo = todo2.copy(isCompleted = true))
        }
    }
}
