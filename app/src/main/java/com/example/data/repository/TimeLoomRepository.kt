package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.algorithms.TimeLoomAlgorithms
import com.example.data.local.SessionDao
import com.example.data.local.TaskDao
import com.example.data.local.TimeLoomDatabase
import com.example.data.model.SessionEntity
import com.example.data.model.SessionStatus
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.model.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ActiveTimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val taskId: Long? = null,
    val taskTitle: String = "General Focus Session",
    val category: String = "Study",
    val startTimestamp: Long = 0L,
    val accumulatedPauseMillis: Long = 0L,
    val pauseStartTimestamp: Long = 0L
)

class TimeLoomRepository(
    private val context: Context,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("time_loom_prefs", Context.MODE_PRIVATE)

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allSessions: Flow<List<SessionEntity>> = sessionDao.getAllSessions()

    init {
        // Preload realistic academic demonstration data on initial install
        CoroutineScope(Dispatchers.IO).launch {
            if (taskDao.getAllTasks().first().isEmpty()) {
                seedAcademicDemoData()
            }
        }
    }

    // -------------------------------------------------------------
    // Task Operations
    // -------------------------------------------------------------
    suspend fun addTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(id: Long) = taskDao.deleteTaskById(id)

    suspend fun updateTaskStatus(id: Long, status: TaskStatus) {
        taskDao.updateTaskStatus(id, status.name)
    }

    suspend fun recordCompletedSession(
        taskId: Long?,
        activityName: String,
        category: String,
        startTime: Long,
        endTime: Long,
        pausedMillis: Long
    ): Long {
        val durationSeconds = TimeLoomAlgorithms.calculateActiveDuration(
            startTimeMillis = startTime,
            endTimeMillis = endTime,
            pausedDurationMillis = pausedMillis
        )
        val todayStr = LocalDate.now().format(TimeLoomAlgorithms.DATE_FORMATTER)

        val session = SessionEntity(
            taskId = taskId,
            activityName = activityName,
            category = category,
            startTime = startTime,
            endTime = endTime,
            durationSeconds = durationSeconds,
            pausedDurationMillis = pausedMillis,
            dateString = todayStr,
            status = SessionStatus.COMPLETED.name
        )

        val sessionId = sessionDao.insertSession(session)

        // Increment actualTrackedDuration on associated task
        if (taskId != null && taskId > 0) {
            val minutes = durationSeconds / 60
            if (minutes > 0) {
                taskDao.incrementTaskTrackedDuration(taskId, minutes)
            }
        }

        return sessionId
    }

    suspend fun deleteSession(id: Long) = sessionDao.deleteSessionById(id)

    // -------------------------------------------------------------
    // Timer Persistence Across App Restarts
    // -------------------------------------------------------------
    fun saveActiveTimer(state: ActiveTimerState) {
        prefs.edit().apply {
            putBoolean("timer_running", state.isRunning)
            putBoolean("timer_paused", state.isPaused)
            putLong("timer_task_id", state.taskId ?: -1L)
            putString("timer_task_title", state.taskTitle)
            putString("timer_category", state.category)
            putLong("timer_start_timestamp", state.startTimestamp)
            putLong("timer_accum_pause", state.accumulatedPauseMillis)
            putLong("timer_pause_start", state.pauseStartTimestamp)
            apply()
        }
    }

    fun loadActiveTimer(): ActiveTimerState {
        val isRunning = prefs.getBoolean("timer_running", false)
        if (!isRunning) return ActiveTimerState()

        val isPaused = prefs.getBoolean("timer_paused", false)
        val taskIdRaw = prefs.getLong("timer_task_id", -1L)
        val taskId = if (taskIdRaw > 0) taskIdRaw else null
        val taskTitle = prefs.getString("timer_task_title", "General Focus Session") ?: "General Focus Session"
        val category = prefs.getString("timer_category", "Study") ?: "Study"
        val startTimestamp = prefs.getLong("timer_start_timestamp", 0L)
        val accumPause = prefs.getLong("timer_accum_pause", 0L)
        val pauseStart = prefs.getLong("timer_pause_start", 0L)

        return ActiveTimerState(
            isRunning = isRunning,
            isPaused = isPaused,
            taskId = taskId,
            taskTitle = taskTitle,
            category = category,
            startTimestamp = startTimestamp,
            accumulatedPauseMillis = accumPause,
            pauseStartTimestamp = pauseStart
        )
    }

    fun clearActiveTimer() {
        prefs.edit().apply {
            putBoolean("timer_running", false)
            putBoolean("timer_paused", false)
            remove("timer_task_id")
            remove("timer_task_title")
            remove("timer_category")
            remove("timer_start_timestamp")
            remove("timer_accum_pause")
            remove("timer_pause_start")
            apply()
        }
    }

    // -------------------------------------------------------------
    // User Preferences
    // -------------------------------------------------------------
    fun getUserPreferences(): UserPreferences {
        return UserPreferences(
            name = prefs.getString("user_name", "Aditya Dandugula") ?: "Aditya Dandugula",
            email = prefs.getString("user_email", "aditya.cs@university.edu") ?: "aditya.cs@university.edu",
            studentId = prefs.getString("user_student_id", "B.Tech CSE - Roll #CS26-104") ?: "B.Tech CSE - Roll #CS26-104",
            dailyGoalMinutes = prefs.getInt("daily_goal_minutes", 120),
            vibrationEnabled = prefs.getBoolean("vibration_enabled", true),
            isDarkMode = prefs.getBoolean("dark_mode", true)
        )
    }

    fun saveUserPreferences(preferences: UserPreferences) {
        prefs.edit().apply {
            putString("user_name", preferences.name)
            putString("user_email", preferences.email)
            putString("user_student_id", preferences.studentId)
            putInt("daily_goal_minutes", preferences.dailyGoalMinutes)
            putBoolean("vibration_enabled", preferences.vibrationEnabled)
            putBoolean("dark_mode", preferences.isDarkMode)
            apply()
        }
    }

    // -------------------------------------------------------------
    // Academic Demonstration Data Preloader
    // -------------------------------------------------------------
    suspend fun seedAcademicDemoData() {
        val today = LocalDate.now()
        val fmt = TimeLoomAlgorithms.DATE_FORMATTER

        val sampleTasks = listOf(
            TaskEntity(
                title = "Data Structures: Graph BFS & DFS Implementation",
                description = "Implement adjacency list representation and cycle detection in undirected graphs for CSE lab assignment.",
                category = TaskCategory.CODING.name,
                priority = TaskPriority.HIGH.name,
                dueDate = today.format(fmt),
                expectedDuration = 60,
                actualTrackedDuration = 45,
                status = TaskStatus.IN_PROGRESS.name
            ),
            TaskEntity(
                title = "Database Systems: 3NF & BCNF Normalization",
                description = "Solve functional dependency decomposition problems for relational schema normalization.",
                category = TaskCategory.STUDY.name,
                priority = TaskPriority.MEDIUM.name,
                dueDate = today.plusDays(1).format(fmt),
                expectedDuration = 45,
                actualTrackedDuration = 45,
                status = TaskStatus.COMPLETED.name
            ),
            TaskEntity(
                title = "Operating Systems: Producer-Consumer Semaphore",
                description = "Write C program simulating bounded buffer synchronization with mutex locks and counting semaphores.",
                category = TaskCategory.CODING.name,
                priority = TaskPriority.URGENT.name,
                dueDate = today.plusDays(2).format(fmt),
                expectedDuration = 90,
                actualTrackedDuration = 0,
                status = TaskStatus.PENDING.name
            ),
            TaskEntity(
                title = "Time Loom: B.Tech Major Project Report",
                description = "Draft Chapter 4 (Algorithmic Design & Complexity Analysis) and Chapter 5 (Results & Benchmarks).",
                category = TaskCategory.PROJECT.name,
                priority = TaskPriority.HIGH.name,
                dueDate = today.plusDays(3).format(fmt),
                expectedDuration = 120,
                actualTrackedDuration = 90,
                status = TaskStatus.IN_PROGRESS.name
            ),
            TaskEntity(
                title = "Research Paper: Time Series Analytics in Quantified Self",
                description = "Literature review of wearable biometric telemetry and temporal heatmaps in productivity tracking.",
                category = TaskCategory.RESEARCH.name,
                priority = TaskPriority.LOW.name,
                dueDate = today.plusDays(7).format(fmt),
                expectedDuration = 60,
                actualTrackedDuration = 0,
                status = TaskStatus.PENDING.name
            ),
            TaskEntity(
                title = "Design Patterns: Observer & Repository Pattern",
                description = "Review GoF structural and behavioral patterns for software engineering viva examination.",
                category = TaskCategory.READING.name,
                priority = TaskPriority.MEDIUM.name,
                dueDate = today.minusDays(1).format(fmt),
                expectedDuration = 40,
                actualTrackedDuration = 40,
                status = TaskStatus.COMPLETED.name
            )
        )

        taskDao.insertTasks(sampleTasks)

        // Seed realistic historical session data for Heatmap (past 30 days) and Reports
        val sampleSessions = mutableListOf<SessionEntity>()
        val currentTime = System.currentTimeMillis()

        // Distribute sessions over past 20 days with varying intensities
        val intensityPlan = listOf(
            0 to 50,    // today: 50m
            1 to 140,   // yesterday: 140m (Level 4!)
            2 to 75,    // 2 days ago: 75m (Level 3)
            3 to 30,    // 3 days ago: 30m (Level 1)
            4 to 90,    // 4 days ago: 90m (Level 3)
            5 to 110,   // 5 days ago: 110m (Level 3)
            6 to 45,    // 6 days ago: 45m (Level 2)
            7 to 130,   // 7 days ago: 130m (Level 4)
            8 to 60,    // 8 days ago: 60m (Level 2)
            9 to 0,     // 9 days ago: rest
            10 to 80,   // 10 days ago: 80m
            12 to 150,  // 12 days ago: 150m
            14 to 45,
            16 to 95,
            18 to 120,
            21 to 65,
            23 to 40,
            25 to 110
        )

        for ((daysAgo, minutes) in intensityPlan) {
            if (minutes <= 0) continue
            val date = today.minusDays(daysAgo.toLong())
            val dateStr = date.format(fmt)
            val sessionStart = currentTime - (daysAgo * 86400000L) - 3600000L
            val durationSec = minutes * 60
            val sessionEnd = sessionStart + (durationSec * 1000L)

            val cat = when (daysAgo % 4) {
                0 -> TaskCategory.CODING.name
                1 -> TaskCategory.PROJECT.name
                2 -> TaskCategory.STUDY.name
                else -> TaskCategory.RESEARCH.name
            }

            sampleSessions.add(
                SessionEntity(
                    taskId = if (daysAgo % 2 == 0) 1L else 2L,
                    activityName = "$cat Sprint: Session #$daysAgo",
                    category = cat,
                    startTime = sessionStart,
                    endTime = sessionEnd,
                    durationSeconds = durationSec,
                    pausedDurationMillis = 0L,
                    dateString = dateStr,
                    status = SessionStatus.COMPLETED.name
                )
            )
        }

        sessionDao.insertSessions(sampleSessions)
    }

    suspend fun resetAllData() {
        taskDao.clearAll()
        sessionDao.clearAll()
        clearActiveTimer()
        seedAcademicDemoData()
    }

    companion object {
        @Volatile
        private var INSTANCE: TimeLoomRepository? = null

        fun getRepository(context: Context): TimeLoomRepository {
            return INSTANCE ?: synchronized(this) {
                val db = TimeLoomDatabase.getDatabase(context)
                val instance = TimeLoomRepository(context, db.taskDao(), db.sessionDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
