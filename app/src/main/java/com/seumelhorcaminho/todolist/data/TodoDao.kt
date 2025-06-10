package com.seumelhorcaminho.todolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data class for the result of the JOIN between Tasks and Categories.
 */
data class TodoWithCategory(
    @Embedded val todo: TodoEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TodoEntity)

    @Delete
    suspend fun delete(entity: TodoEntity)

    /**
     * Retrieves all tasks with their respective categories.
     * The @Transaction ensures the retrieval of the task and category is atomic.
     */
    @Transaction
    @Query("SELECT * FROM todos ORDER BY id DESC") // Added ORDER BY to show the newest ones first
    fun getAllWithCategory(): Flow<List<TodoWithCategory>>

    /**
     * Essential for the edit screen.
     */
    @Transaction
    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getByIdWithCategory(id: Long): TodoWithCategory?

}
