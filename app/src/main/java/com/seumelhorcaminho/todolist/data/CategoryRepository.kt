package com.seumelhorcaminho.todolist.data

import com.seumelhorcaminho.todolist.domain.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    /**
     * Retorna um Flow com a lista de todas as categorias.
     */
    fun getAll(): Flow<List<Category>>

    /**
     * Insere uma nova categoria no banco de dados.
     */
    suspend fun insert(category: Category)

}