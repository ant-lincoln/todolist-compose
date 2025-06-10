package com.seumelhorcaminho.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val priority: String,
    val categoryId: Long,
    val checklistJson: String?,
)
