package com.seumelhorcaminho.todolist.ui.feature.list

import com.seumelhorcaminho.todolist.domain.Todo

sealed interface ListEvent {
    data class OnDeleteClick(val todo: Todo) : ListEvent
    data class OnCompleteChange(val todo: Todo, val isCompleted: Boolean) : ListEvent
    data class OnItemClick(val id: Long?) : ListEvent

    data class OnAddCategory(val name: String, val emoji: String) : ListEvent
}