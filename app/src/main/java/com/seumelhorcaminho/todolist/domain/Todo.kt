package com.seumelhorcaminho.todolist.domain

enum class Priority { BAIXA, MÉDIA, ALTA }

data class Todo(
    val id: Long,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val priority: Priority,
    val category: Category,
    val checklist: List<ChecklistItem>,
)

data class Category(
    val id: Long,
    val name: String,
    val emoji: String,
)

