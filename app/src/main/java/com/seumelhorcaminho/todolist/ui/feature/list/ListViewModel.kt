package com.seumelhorcaminho.todolist.ui.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seumelhorcaminho.todolist.data.CategoryRepository
import com.seumelhorcaminho.todolist.data.TodoRepository
import com.seumelhorcaminho.todolist.domain.Category
import com.seumelhorcaminho.todolist.domain.Todo
import com.seumelhorcaminho.todolist.navigation.AddEditRoute
import com.seumelhorcaminho.todolist.ui.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)

    val groupedTodos = todoRepository.getAll()
        .combine(_selectedCategory) { todos, selectedCategory ->
            val filteredTodos = if (selectedCategory == null) {
                todos
            } else {
                todos.filter { it.category.id == selectedCategory.id }
            }
            val (active, completed) = filteredTodos.partition { !it.isCompleted }
            mapOf("Ativas" to active, "Concluídas" to completed)
                .filter { it.value.isNotEmpty() }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val categories = categoryRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: ListEvent) {
        when (event) {
            is ListEvent.OnDeleteClick -> {
                delete(event.todo)
            }

            is ListEvent.OnCompleteChange -> {
                val updatedTodo = event.todo.copy(isCompleted = event.isCompleted)
                update(updatedTodo)
            }

            is ListEvent.OnItemClick -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(AddEditRoute(event.id)))
                }
            }

            is ListEvent.OnAddCategory -> {
                addCategory(event.name, event.emoji)
            }

            is ListEvent.OnCategorySelected -> {
                _selectedCategory.value = event.category
            }

            is ListEvent.OnDeleteCategoryConfirm -> {
                deleteCategory(event.category)
            }
        }
    }

    private fun addCategory(name: String, emoji: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                val newCategory = Category(id = 0, name = name, emoji = emoji)
                categoryRepository.insert(newCategory)
            }
        }
    }

    private fun deleteCategory(category: Category) {
        viewModelScope.launch {
            /* TODO: Pensar o que fazer com as tarefas
            * que pertencem a esta categoria?
            * Por enquanto só deleta, mas depois acho que fica melhor mover
            * para uma categoria geralzona*/
            categoryRepository.delete(category)
        }
    }

    private fun delete(todo: Todo) {
        viewModelScope.launch {
            todoRepository.delete(todo)
        }
    }

    private fun update(todo: Todo) {
        viewModelScope.launch {
            todoRepository.insert(todo)
        }
    }
}