package com.seumelhorcaminho.todolist.ui.feature.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seumelhorcaminho.todolist.R
import com.seumelhorcaminho.todolist.data.TodoDatabaseProvider
import com.seumelhorcaminho.todolist.data.TodoRepositoryImpl
import com.seumelhorcaminho.todolist.domain.Todo
import com.seumelhorcaminho.todolist.domain.todo1
import com.seumelhorcaminho.todolist.domain.todo2
import com.seumelhorcaminho.todolist.navigation.AddEditRoute
import com.seumelhorcaminho.todolist.ui.UiEvent
import com.seumelhorcaminho.todolist.ui.components.AddCategorySheetContent
import com.seumelhorcaminho.todolist.ui.components.AppDrawerContent
import com.seumelhorcaminho.todolist.ui.components.TodoItem
import com.seumelhorcaminho.todolist.ui.theme.TodoListTheme
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTopBar(
    onMenuClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tiks",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            IconButton(onClick = onCalendarClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Calendário"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            colorResource(id = R.color.grey_50)
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContent(
    todos: List<Todo>,
    onEvent: (ListEvent) -> Unit,
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                onItemSelected = { selectedItem ->
                    println("Item selecionado: $selectedItem")
                    //TODO: LOGICA DE NAVEGACAO OU FILTRO
                },
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onAddCategoryClick = {
                    showBottomSheet = true
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {

        Scaffold(
            topBar = {
                TodoTopBar(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onCalendarClick = {
                        // Handle calendar click
                    }
                )
            },
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
                modifier = Modifier
                    .padding(paddingValues),
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

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    AddCategorySheetContent(
                        onSaveClick = { name, emoji ->
                            println("Salvar Categoria: Nome=$name, Emoji=$emoji")

                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        },
                        onEmojiClick = {
                            println("Adicionar Emoji")
                        }
                    )
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