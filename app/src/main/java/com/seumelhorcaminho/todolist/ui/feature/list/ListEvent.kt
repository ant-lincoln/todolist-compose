package com.seumelhorcaminho.todolist.ui.feature.list

sealed interface ListEvent {
    data class OnDeleteClick(val id: Long) : ListEvent
    data class OnCompleteChange(val id: Long, val isCompleted: Boolean) : ListEvent
    data class OnItemClick(val id: Long?) : ListEvent
}