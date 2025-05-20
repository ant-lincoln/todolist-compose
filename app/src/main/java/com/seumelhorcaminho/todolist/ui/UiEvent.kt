package com.seumelhorcaminho.todolist.ui

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data object PopBack : UiEvent
    data class Navigate<T : Any>(val route: T) : UiEvent

}