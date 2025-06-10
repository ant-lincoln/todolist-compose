package com.seumelhorcaminho.todolist.ui.feature.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seumelhorcaminho.todolist.R
import com.seumelhorcaminho.todolist.data.CategoryRepositoryImpl
import com.seumelhorcaminho.todolist.data.TodoDatabaseProvider
import com.seumelhorcaminho.todolist.data.TodoRepositoryImpl
import com.seumelhorcaminho.todolist.domain.Todo
import com.seumelhorcaminho.todolist.navigation.AddEditRoute
import com.seumelhorcaminho.todolist.ui.UiEvent
import com.seumelhorcaminho.todolist.ui.components.AddCategorySheetContent
import com.seumelhorcaminho.todolist.ui.components.AppDrawerContent
import com.seumelhorcaminho.todolist.ui.components.TodoItem
import kotlinx.coroutines.launch

@Composable
fun ListScreen(
    navigateToAddEditScreen: (id: Long?) -> Unit
) {
    val context = LocalContext.current.applicationContext
    val database = TodoDatabaseProvider.provide(context)
    val todoRepository = TodoRepositoryImpl(dao = database.todoDao)
    val categoryRepository = CategoryRepositoryImpl(dao = database.categoryDao)

    val viewModel = viewModel<ListViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ListViewModel(
                    todoRepository = todoRepository,
                    categoryRepository = categoryRepository
                ) as T
            }
        }
    )

    val groupedTodos by viewModel.groupedTodos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate<*> -> {
                    when (event.route) {
                        is AddEditRoute -> navigateToAddEditScreen(event.route.id)
                    }
                }

                else -> {}
            }
        }
    }

    ListContent(
        groupedTodos = groupedTodos,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTopBar(
    onMenuClick: () -> Unit,
    onCalendarClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(painter = painterResource(id = R.drawable.ic_menu), "Menu")
            }
        },
        actions = {
            IconButton(onClick = onCalendarClick) {
                Icon(painter = painterResource(id = R.drawable.ic_calendar), "Calendário")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListContent(
    groupedTodos: Map<String, List<Todo>>,
    onEvent: (ListEvent) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                onItemSelected = { /* TODO: Lógica de filtro/navegação */ },
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
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onCalendarClick = { /* TODO */ }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onEvent(ListEvent.OnItemClick(null)) },
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (groupedTodos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Nenhuma tarefa ainda.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    groupedTodos.forEach { (header, todosInGroup) ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = header,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        items(todosInGroup, key = { it.id }) { todo ->
                            TodoItem(
                                todo = todo,
                                onCompleteChange = { isCompleted ->
                                    onEvent(ListEvent.OnCompleteChange(todo, isCompleted))
                                },
                                onItemClick = {
                                    onEvent(ListEvent.OnItemClick(todo.id))
                                },
                                onDeleteClick = {
                                    onEvent(ListEvent.OnDeleteClick(todo))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            AddCategorySheetContent(
                onSaveClick = { name, emoji ->
                    onEvent(ListEvent.OnAddCategory(name, emoji))
                    scope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                onEmojiClick = { /* TODO */ }
            )
        }
    }
}