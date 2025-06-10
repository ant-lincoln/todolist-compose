package com.seumelhorcaminho.todolist.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seumelhorcaminho.todolist.domain.Category
import com.seumelhorcaminho.todolist.domain.ChecklistItem
import com.seumelhorcaminho.todolist.domain.Priority
import com.seumelhorcaminho.todolist.domain.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(
    private val dao: TodoDao
) : TodoRepository {

    override fun getAll(): Flow<List<Todo>> {
        return dao.getAllWithCategory().map { listTodoWithCategory ->
            listTodoWithCategory.map { todoWithCategory ->
                mapToDomain(todoWithCategory)
            }
        }
    }

    override suspend fun getById(id: Long): Todo? {
        return dao.getByIdWithCategory(id)?.let { todoWithCategory ->
            mapToDomain(todoWithCategory)
        }
    }

    override suspend fun insert(todo: Todo) {
        val entity = TodoEntity(
            id = todo.id,
            title = todo.title,
            description = todo.description,
            isCompleted = todo.isCompleted,
            priority = todo.priority.name,
            categoryId = todo.category.id,
            checklistJson = todo.checklist?.let { Gson().toJson(it) }
        )
        dao.insert(entity)
    }

    override suspend fun delete(todo: Todo) {
        val entityToDelete = TodoEntity(
            id = todo.id,
            title = todo.title,
            description = todo.description,
            isCompleted = todo.isCompleted,
            priority = todo.priority.name,
            categoryId = todo.category.id,
            checklistJson = todo.checklist?.let { Gson().toJson(it) }
        )
        dao.delete(entityToDelete)
    }

    /**
     * Helper function to avoid code duplication in the mapping.
     */
    private fun mapToDomain(todoWithCategory: TodoWithCategory): Todo {
        return Todo(
            id = todoWithCategory.todo.id,
            title = todoWithCategory.todo.title,
            description = todoWithCategory.todo.description,
            isCompleted = todoWithCategory.todo.isCompleted,
            priority = Priority.valueOf(todoWithCategory.todo.priority),
            category = Category(
                id = todoWithCategory.category.id,
                name = todoWithCategory.category.name,
                emoji = todoWithCategory.category.emoji
            ),
            checklist = todoWithCategory.todo.checklistJson?.let { json ->
                Gson().fromJson<List<ChecklistItem>>(json, object : TypeToken<List<ChecklistItem>>() {}.type)
            } ?: emptyList()
        )
    }
}