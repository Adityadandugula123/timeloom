package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

enum class TaskPriority(val displayName: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4)
}

enum class TaskCategory(val displayName: String) {
    STUDY("Study"),
    CODING("Coding"),
    PROJECT("Project"),
    RESEARCH("Research"),
    READING("Reading"),
    FITNESS("Fitness"),
    OTHER("Other")
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "user_default",
    val title: String,
    val description: String = "",
    val category: String = TaskCategory.PROJECT.name,
    val priority: String = TaskPriority.MEDIUM.name,
    val dueDate: String = "", // Format: YYYY-MM-DD
    val expectedDuration: Int = 30, // in minutes
    val actualTrackedDuration: Int = 0, // in minutes
    val status: String = TaskStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
