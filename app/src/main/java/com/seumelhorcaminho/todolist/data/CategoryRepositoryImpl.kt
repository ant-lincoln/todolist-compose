package com.seumelhorcaminho.todolist.data

import com.seumelhorcaminho.todolist.domain.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getAll(): Flow<List<Category>> {
        return dao.getAll().map { entities ->
            entities.map { entity ->
                Category(
                    id = entity.id,
                    name = entity.name,
                    emoji = entity.emoji
                )
            }
        }
    }

    override suspend fun insert(category: Category) {
        val entity = CategoryEntity(
            id = category.id,
            name = category.name,
            emoji = category.emoji
        )
        dao.insert(entity)
    }

    override suspend fun delete(category: Category) {
        val entity = CategoryEntity(
            id = category.id,
            name = category.name,
            emoji = category.emoji
        )
        dao.delete(entity)
    }
}