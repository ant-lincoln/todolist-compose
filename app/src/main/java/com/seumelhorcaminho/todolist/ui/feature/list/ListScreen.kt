package com.seumelhorcaminho.todolist.ui.feature.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seumelhorcaminho.todolist.data.TodoDatabaseProvider
import com.seumelhorcaminho.todolist.data.TodoRepositoryImpl
import com.seumelhorcaminho.todolist.domain.Todo
import com.seumelhorcaminho.todolist.domain.todo1
import com.seumelhorcaminho.todolist.domain.todo2
import com.seumelhorcaminho.todolist.navigation.AddEditRoute
import com.seumelhorcaminho.todolist.ui.UiEvent
import com.seumelhorcaminho.todolist.ui.components.TodoItem
import com.seumelhorcaminho.todolist.ui.theme.TodoListTheme

@Composable
fun ListScreen(
    navigateToAddEditScreen: (id: Long?) -> Unit
) {
    val context = LocalContext.current.applicationContext
    val database = TodoDatabaseProvider.provide(context)
    val repository = TodoRepositoryImpl(
        dao = database.todoDao
    )

    val viewModel = viewModel<ListViewModel>() {
        ListViewModel(repository = repository)
    }

    val todos by viewModel.todos.collectAsState()



    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate<*> -> {
                    when (event.route) {
                        is AddEditRoute -> {
                            navigateToAddEditScreen(event.route.id)
                        }
                    }
                }

                UiEvent.PopBack -> {
                }

                is UiEvent.ShowSnackbar -> {
                }
            }
        }
    }

    ListContent(
        todos = todos,
        onEvent = viewModel::onEvent
    )

}


@Composable
fun ListContent(
    todos: List<Todo>,
    onEvent: (ListEvent) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(
                    ListEvent.OnItemClick(null)
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(paddingValues),
            contentPadding = PaddingValues(16.dp),
        ) {
            itemsIndexed(todos) { index, todo ->
                TodoItem(
                    todo = todo,
                    onCompleteChange = {
                        onEvent(
                            ListEvent.OnCompleteChange(
                                todo.id,
                                isCompleted = it,
                            )
                        )
                    },
                    onItemClick = {
                        onEvent(
                            ListEvent.OnItemClick(
                                todo.id
                            )
                        )
                    },
                    onDeleteClick = {
                        onEvent(
                            ListEvent.OnDeleteClick(
                                todo.id
                            )
                        )
                    },
                )

                if (index < todos.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ListContentPreview() {
    TodoListTheme {
        ListContent(
            todos = listOf(todo1, todo2),
            onEvent = {},
        )

    }
}