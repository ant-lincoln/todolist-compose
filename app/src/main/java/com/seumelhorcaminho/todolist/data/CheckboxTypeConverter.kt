package com.seumelhorcaminho.todolist.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seumelhorcaminho.todolist.domain.ChecklistItem

class CheckboxTypeConverter {
    @TypeConverter
    fun fromChecklistJson(json: String?): List<ChecklistItem>? {
        if (json == null) return null
        val type = object : TypeToken<List<ChecklistItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toChecklistJson(checklist: List<ChecklistItem>?): String? {
        if (checklist == null) return null
        return Gson().toJson(checklist)
    }
}
