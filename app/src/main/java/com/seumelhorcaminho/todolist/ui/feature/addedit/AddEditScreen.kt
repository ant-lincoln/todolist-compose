package com.seumelhorcaminho.todolist.ui.feature.addedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seumelhorcaminho.todolist.data.TodoDatabaseProvider
import com.seumelhorcaminho.todolist.data.TodoRepositoryImpl
import com.seumelhorcaminho.todolist.ui.UiEvent
import com.seumelhorcaminho.todolist.ui.theme.TodoListTheme

@Composable
fun AddEditScreen(
    id: Long? = null,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current.applicationContext
    val database = TodoDatabaseProvider.provide(context)
    val repository = TodoRepositoryImpl(
        dao = database.todoDao
    )
    val viewModel = viewModel<AddEditViewModel>() {
        AddEditViewModel(
            id = id,
            repository = repository)
    }

    val title = viewModel.title
    val description = viewModel.description

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }

                UiEvent.PopBack -> {
                    navigateBack()
                }

                is UiEvent.Navigate<*> -> {

                }
            }
        }
    }

    AddEditContent(
        title = title,
        description = description,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun AddEditContent(
    title: String,
    description: String?,
    snackbarHostState: SnackbarHostState,
    onEvent: (AddEditEvent) -> Unit = {},
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(AddEditEvent.Save)
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = title,
                onValueChange = {
                    onEvent(
                        AddEditEvent.TitleChanged(it)
                    )
                },
                placeholder = { Text(text = "Title") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = description ?: "\uD83C\uDF43",
                onValueChange = {
                    onEvent(
                        AddEditEvent.DescriptionChanged(it)
                    )
                },
                placeholder = { Text(text = "Description (optional)") }
            )

        }
    }

}

@Preview
@Composable
private fun AddEditScreenPreview() {
    TodoListTheme {
        AddEditContent(
            title = "",
            description = "",
            snackbarHostState = SnackbarHostState(),
            onEvent = {}
        )

    }
}