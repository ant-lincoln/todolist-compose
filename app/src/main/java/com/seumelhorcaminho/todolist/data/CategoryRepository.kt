package com.seumelhorcaminho.todolist.data

import com.seumelhorcaminho.todolist.domain.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    /**
     * Returns a Flow with the list of all categories.
     */
    fun getAll(): Flow<List<Category>>

    /**
     * Inserts a new category into the database.
     */
    suspend fun insert(category: Category)

    suspend fun delete(category: Category)


}
