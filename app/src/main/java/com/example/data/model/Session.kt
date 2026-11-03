package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SessionStatus {
    ACTIVE,
    PAUSED,
    COMPLETED,
    DISCARDED
}

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "user_default",
    val taskId: Long? = null,
    val activityName: String,
    val category: String = "Study",
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Int,
    val pausedDurationMillis: Long = 0L,
    val dateString: String, // YYYY-MM-DD in local time
    val status: String = SessionStatus.COMPLETED.name,
    val createdAt: Long = System.currentTimeMillis()
)
