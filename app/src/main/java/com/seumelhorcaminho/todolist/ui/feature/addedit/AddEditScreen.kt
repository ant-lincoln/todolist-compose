package com.seumelhorcaminho.todolist.ui.feature.addedit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seumelhorcaminho.todolist.R
import com.seumelhorcaminho.todolist.data.CategoryRepositoryImpl
import com.seumelhorcaminho.todolist.data.TodoDatabaseProvider
import com.seumelhorcaminho.todolist.data.TodoRepositoryImpl
import com.seumelhorcaminho.todolist.domain.Category
import com.seumelhorcaminho.todolist.domain.ChecklistItem
import com.seumelhorcaminho.todolist.domain.Priority
import com.seumelhorcaminho.todolist.ui.UiEvent
import kotlin.math.max

@Composable
fun AddEditScreen(
    id: Long?,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current.applicationContext
    val database = TodoDatabaseProvider.provide(context)
    val todoRepository = TodoRepositoryImpl(dao = database.todoDao)
    val categoryRepository = CategoryRepositoryImpl(dao = database.categoryDao)

    val viewModel = viewModel<AddEditViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddEditViewModel(
                    todoRepository = todoRepository,
                    categoryRepository = categoryRepository,
                    todoId = id
                ) as T
            }
        }
    )

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(message = event.message)
                is UiEvent.PopBack -> navigateBack()
                else -> {}
            }
        }
    }

    AddEditContent(
        state = viewModel,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContent(
    state: AddEditViewModel,
    onEvent: (AddEditEvent) -> Unit,
    navigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditing()) "Edit task" else "Add task",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { onEvent(AddEditEvent.Save) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                // Desabilita o botão se o título estiver em branco ou nenhuma categoria for selecionada
                enabled = state.title.isNotBlank() && state.selectedCategory != null
            ) {
                Text("Salvar task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { CategorySelector(state.categories, state.selectedCategory, onEvent) }
            item {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { onEvent(AddEditEvent.TitleChanged(it)) },
                    label = { Text("Add título") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { PrioritySelector(state.priority, onEvent) }
            item {
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { onEvent(AddEditEvent.DescriptionChanged(it)) },
                    label = { Text("Add description (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            item { ChecklistEditor(state.checklist, onEvent) }
        }
    }
}

// Adicionei esta função ao ViewModel para verificar se está em modo de edição
fun AddEditViewModel.isEditing() = this.todoId != null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
    categories: List<Category>,
    selectedCategory: Category?,
    onEvent: (AddEditEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory?.let { "${it.emoji} ${it.name}" } ?: "Selecione uma Categoria",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text("${category.emoji} ${category.name}") },
                    onClick = {
                        onEvent(AddEditEvent.CategoryChanged(category))
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: Priority,
    onEvent: (AddEditEvent) -> Unit
) {
    Column {
        Text(
            "Prioridade",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Priority.entries.forEach { priority ->
                val isSelected = selectedPriority == priority

                val priorityColor = when (priority) {
                    Priority.ALTA -> colorResource(R.color.red)
                    Priority.MÉDIA -> colorResource(R.color.yellow)
                    else -> colorResource(R.color.green)
                }
                OutlinedButton(
                    onClick = { onEvent(AddEditEvent.PriorityChanged(priority)) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) priorityColor else Color.Transparent,
                        contentColor = if (isSelected) Color.White else priorityColor
                    ),
                    border = BorderStroke(1.dp, priorityColor)
                ) {
                    Text(priority.name)
                }
            }
        }
    }
}

@Composable
private fun ChecklistEditor(
    checklist: List<ChecklistItem>,
    onEvent: (AddEditEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Add checkbox (opcional)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        checklist.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = {
                        onEvent(
                            AddEditEvent.ChecklistItemCheckedChanged(
                                index,
                                it
                            )
                        )
                    }
                )
                OutlinedTextField(
                    value = item.text,
                    onValueChange = { onEvent(AddEditEvent.ChecklistItemTextChanged(index, it)) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Novo item") }
                )
                IconButton(onClick = { onEvent(AddEditEvent.DeleteChecklistItem(index)) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Deletar item")
                }
            }
        }
        TextButton(onClick = { onEvent(AddEditEvent.AddChecklistItem) }) {
            Text("+ Adicionar item")
        }
    }
}