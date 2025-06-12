package com.seumelhorcaminho.todolist.ui.feature.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seumelhorcaminho.todolist.R
import com.seumelhorcaminho.todolist.data.CategoryRepositoryImpl
import com.seumelhorcaminho.todolist.data.TodoDatabaseProvider
import com.seumelhorcaminho.todolist.data.TodoRepositoryImpl
import com.seumelhorcaminho.todolist.domain.Category
import com.seumelhorcaminho.todolist.domain.Todo
import com.seumelhorcaminho.todolist.navigation.AddEditRoute
import com.seumelhorcaminho.todolist.ui.UiEvent
import com.seumelhorcaminho.todolist.ui.components.AddCategorySheetContent
import com.seumelhorcaminho.todolist.ui.components.AppDrawerContent
import com.seumelhorcaminho.todolist.ui.components.EmojiPickerSheetContent
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
    val categories by viewModel.categories.collectAsState()

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
        categories = categories,
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
    categories: List<Category>,
    onEvent: (ListEvent) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddCategorySheet by remember { mutableStateOf(false) }
    var showEmojiPickerSheet by remember { mutableStateOf(false) }
    var newCategoryName by rememberSaveable { mutableStateOf("") }
    var selectedEmoji by rememberSaveable { mutableStateOf("⭐") }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    val addCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val emojiPickerSheetState = rememberModalBottomSheetState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                categories = categories,
                onCategorySelected = { category ->
                    onEvent(ListEvent.OnCategorySelected(category))
                },
                onCategoryLongPress = { category ->
                    categoryToDelete = category
                    scope.launch { drawerState.close() }
                },
                onStaticItemSelected = { itemName ->
                    // TODO: Implementar lógica para filtros estáticos ("Hoje", etc) no ViewModel
                    println("Item estático selecionado: $itemName")
                },
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onAddCategoryClick = {
                    newCategoryName = ""
                    selectedEmoji = "⭐"
                    showAddCategorySheet = true
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
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.no_task_new),
                                    contentDescription = "Sem tarefas"
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Está calmo por aqui\n\nʕ ˵• ₒ •˵ ʔ",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Light,
                                    textAlign = TextAlign.Center,
                                    color = colorResource(R.color.grey_70)
                                )
                            }
                        }
                    }
                } else {
                    groupedTodos.forEach { (header, todosInGroup) ->
                        stickyHeader {
                            Surface(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
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


    if (showAddCategorySheet) {
        ModalBottomSheet(onDismissRequest = { showAddCategorySheet = false }, sheetState = addCategorySheetState) {
            AddCategorySheetContent(
                categoryName = newCategoryName,
                selectedEmoji = selectedEmoji,
                onCategoryNameChange = { newCategoryName = it },
                onEmojiClick = { showEmojiPickerSheet = true },
                onSaveClick = {
                    onEvent(ListEvent.OnAddCategory(newCategoryName, selectedEmoji))
                    scope.launch { addCategorySheetState.hide().also { showAddCategorySheet = false } }
                }
            )
        }
    }

    if (showEmojiPickerSheet) {
        ModalBottomSheet(onDismissRequest = { showEmojiPickerSheet = false }, sheetState = emojiPickerSheetState) {
            EmojiPickerSheetContent(
                onEmojiSelected = { emoji ->
                    selectedEmoji = emoji
                    scope.launch { emojiPickerSheetState.hide().also { showEmojiPickerSheet = false } }
                }
            )
        }
    }

    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Deseja excluir a categoria '${categoryToDelete!!.name}'? As tarefas nesta categoria não serão excluídas, mas ficarão sem categoria.") },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(ListEvent.OnDeleteCategoryConfirm(categoryToDelete!!))
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Excluir") }
            },
            dismissButton = {
                Button(onClick = { categoryToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}