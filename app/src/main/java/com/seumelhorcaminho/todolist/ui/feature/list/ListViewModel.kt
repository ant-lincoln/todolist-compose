package com.seumelhorcaminho.todolist.ui.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seumelhorcaminho.todolist.data.CategoryRepository
import com.seumelhorcaminho.todolist.data.TodoRepository
import com.seumelhorcaminho.todolist.domain.Category
import com.seumelhorcaminho.todolist.navigation.AddEditRoute
import com.seumelhorcaminho.todolist.ui.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val groupedTodos = todoRepository.getAll().map { todos ->
        val (active, completed) = todos.partition { !it.isCompleted }
        mapOf("Ativas" to active, "Concluídas" to completed)
            .filter { it.value.isNotEmpty() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
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

    private fun delete(todo: com.seumelhorcaminho.todolist.domain.Todo) {
        viewModelScope.launch {
            todoRepository.delete(todo)
        }
    }

    private fun update(todo: com.seumelhorcaminho.todolist.domain.Todo) {
        viewModelScope.launch {
            todoRepository.insert(todo)
        }
    }
}