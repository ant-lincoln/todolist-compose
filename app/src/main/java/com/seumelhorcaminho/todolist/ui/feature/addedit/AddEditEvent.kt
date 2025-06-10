package com.seumelhorcaminho.todolist.ui.feature.addedit

import com.seumelhorcaminho.todolist.domain.Category
import com.seumelhorcaminho.todolist.domain.Priority

/**
 * Defines all events that can be triggered from the AddEditScreen.
 */

sealed interface AddEditEvent {
    data class TitleChanged(val title: String) : AddEditEvent
    data class DescriptionChanged(val description: String) : AddEditEvent
    data class CategoryChanged(val category: Category) : AddEditEvent
    data class PriorityChanged(val priority: Priority) : AddEditEvent
    data class ChecklistItemTextChanged(val index: Int, val text: String) : AddEditEvent
    data class ChecklistItemCheckedChanged(val index: Int, val isChecked: Boolean) : AddEditEvent
    data object AddChecklistItem : AddEditEvent
    data class DeleteChecklistItem(val index: Int) : AddEditEvent
    data object Save : AddEditEvent
}