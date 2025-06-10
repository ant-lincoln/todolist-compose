package com.seumelhorcaminho.todolist.ui.feature.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seumelhorcaminho.todolist.data.CategoryRepository
import com.seumelhorcaminho.todolist.data.TodoRepository
import com.seumelhorcaminho.todolist.domain.Category
import com.seumelhorcaminho.todolist.domain.ChecklistItem
import com.seumelhorcaminho.todolist.domain.Priority
import com.seumelhorcaminho.todolist.domain.Todo
import com.seumelhorcaminho.todolist.ui.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    val todoId: Long?
) : ViewModel() {

    // --- UI STATES ---
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var priority by mutableStateOf(Priority.BAIXA)
        private set
    var checklist by mutableStateOf<List<ChecklistItem>>(emptyList())
        private set
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set
    var selectedCategory by mutableStateOf<Category?>(null)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            categories = categoryRepository.getAll().first()

            if (todoId != null) {
                todoRepository.getById(todoId)?.let { todo ->
                    title = todo.title
                    description = todo.description ?: ""
                    priority = todo.priority
                    selectedCategory = todo.category
                    checklist = todo.checklist
                }
            } else {
                selectedCategory = categories.firstOrNull()
            }
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.TitleChanged -> title = event.title
            is AddEditEvent.DescriptionChanged -> description = event.description
            is AddEditEvent.CategoryChanged -> selectedCategory = event.category
            is AddEditEvent.PriorityChanged -> priority = event.priority
            is AddEditEvent.AddChecklistItem -> checklist = checklist + ChecklistItem("", false)
            is AddEditEvent.DeleteChecklistItem -> checklist =
                checklist.toMutableList().also { it.removeAt(event.index) }

            is AddEditEvent.ChecklistItemTextChanged -> {
                checklist = checklist.toMutableList().also {
                    it[event.index] = it[event.index].copy(text = event.text)
                }
            }

            is AddEditEvent.ChecklistItemCheckedChanged -> {
                checklist = checklist.toMutableList().also {
                    it[event.index] = it[event.index].copy(isChecked = event.isChecked)
                }
            }

            is AddEditEvent.Save -> saveTodo()
        }
    }

    private fun saveTodo() {
        viewModelScope.launch {
            if (title.isBlank() || selectedCategory == null) {
                _uiEvent.send(UiEvent.ShowSnackbar("Título e Categoria são obrigatórios."))
                return@launch
            }

            val isCompleted = if (todoId != null) {
                todoRepository.getById(todoId)?.isCompleted ?: false
            } else {
                false
            }

            val todoToSave = Todo(
                id = todoId ?: 0,
                title = title,
                description = description.takeIf { it.isNotBlank() },
                priority = priority,
                category = selectedCategory!!,
                checklist = checklist,
                isCompleted = isCompleted
            )

            todoRepository.insert(todoToSave)
            _uiEvent.send(UiEvent.PopBack)
        }
    }
}