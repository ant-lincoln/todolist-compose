package com.seumelhorcaminho.todolist.data

import com.seumelhorcaminho.todolist.domain.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    /**
     * Returns a Flow with the list of all tasks and their categories.
     */
    fun getAll(): Flow<List<Todo>>

    /**
     * Fetches a single task by its ID, including its category.
     */
    suspend fun getById(id: Long): Todo?

    /**
     * Inserts a new task or updates an existing one if the ID already exists.
     */
    suspend fun insert(todo: Todo)

    /**
     * Deletes a task.
     */
    suspend fun delete(todo: Todo)

}
