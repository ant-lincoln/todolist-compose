package com.seumelhorcaminho.todolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TodoEntity::class, CategoryEntity::class],
    version = 2,
)
@TypeConverters(CheckboxTypeConverter::class)
abstract class TodoDatabase : RoomDatabase(){
    abstract val todoDao: TodoDao
    abstract val categoryDao: CategoryDao
}

object TodoDatabaseProvider {

    @Volatile
    private var INSTANCE: TodoDatabase? = null

    fun provide(context: Context): TodoDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                "todo-app"
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}