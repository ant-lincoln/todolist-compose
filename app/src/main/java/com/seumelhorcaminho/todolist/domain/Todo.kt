package com.seumelhorcaminho.todolist.domain

data class Todo(
    val id: Long,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
)

//Fake objects
val todo1 = Todo(
    id = 1,
    title = "Todo 1",
    description = "Description 1",
    isCompleted = false,
)

val todo2 = Todo(
    id = 2,
    title = "Todo 2",
    description = "Description 2",
    isCompleted = false,
)
