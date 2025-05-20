package com.seumelhorcaminho.todolist.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Surface(
        onClick = onItemClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = onCompleteChange
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = todo.description.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        if (it.isNullOrBlank()) {
                            "\uD83C\uDF43"
                        } else {
                            it
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }

    }

}

@Preview
@Composable
private fun TodoItemPreview() {
    TodoListTheme {
        TodoItem(
            todo = todo1,
            onCompleteChange = {},
            onItemClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview
@Composable
private fun TodoItemCompletedPreview() {
    TodoListTheme {
        TodoItem(
            todo = todo2,
            onCompleteChange = {},
            onItemClick = {},
            onDeleteClick = {}
        )
    }
}
