package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithms.TimeLoomAlgorithms
import com.example.data.model.SessionEntity
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.model.UserPreferences
import com.example.data.repository.ActiveTimerState
import com.example.data.repository.TimeLoomRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class TimeLoomTab(val title: String) {
    HOME("Home"),
    TASKS("Tasks"),
    TIMER("Timer"),
    REPORTS("Reports"),
    HEATMAP("Heatmap")
}

class TimeLoomViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TimeLoomRepository.getRepository(application)

    // Data streams from Room
    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSessions: StateFlow<List<SessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation State
    private val _currentTab = MutableStateFlow(TimeLoomTab.HOME)
    val currentTab = _currentTab.asStateFlow()

    private val _showSettings = MutableStateFlow(false)
    val showSettings = _showSettings.asStateFlow()

    private val _showVivaScreen = MutableStateFlow(false)
    val showVivaScreen = _showVivaScreen.asStateFlow()

    private val _showScoreModal = MutableStateFlow(false)
    val showScoreModal = _showScoreModal.asStateFlow()

    private val _editingTask = MutableStateFlow<TaskEntity?>(null)
    val editingTask = _editingTask.asStateFlow()

    private val _isAddEditDialogOpen = MutableStateFlow(false)
    val isAddEditDialogOpen = _isAddEditDialogOpen.asStateFlow()

    // Task Filter & Search States
    val taskSearchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("All")
    val selectedStatusFilter = MutableStateFlow("All")

    // Filtered Tasks via Algorithm 2
    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        allTasks,
        taskSearchQuery,
        selectedCategoryFilter,
        selectedStatusFilter
    ) { tasks, query, cat, stat ->
        TimeLoomAlgorithms.filterTasks(
            tasks = tasks,
            query = query,
            category = cat,
            status = stat
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Preferences
    private val _userPreferences = MutableStateFlow(repository.getUserPreferences())
    val userPreferences = _userPreferences.asStateFlow()

    // Active Focus Timer State
    private val _activeTimerState = MutableStateFlow(repository.loadActiveTimer())
    val activeTimerState = _activeTimerState.asStateFlow()

    private val _timerDisplaySeconds = MutableStateFlow(0)
    val timerDisplaySeconds = _timerDisplaySeconds.asStateFlow()

    private var timerJob: Job? = null

    // Calendar Heatmap State
    val heatmapMonthOffset = MutableStateFlow(0) // 0 = current month, -1 = last month, etc.
    val selectedHeatmapDate = MutableStateFlow<LocalDate?>(LocalDate.now())

    val currentHeatmapDate: StateFlow<LocalDate> = combine(heatmapMonthOffset) { (offset) ->
        LocalDate.now().plusMonths(offset.toLong())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDate.now())

    val monthHeatmap: StateFlow<List<TimeLoomAlgorithms.HeatmapDay>> = combine(
        allSessions,
        currentHeatmapDate
    ) { sessions, date ->
        TimeLoomAlgorithms.generateMonthHeatmap(
            sessions = sessions,
            year = date.year,
            month = date.monthValue
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDateSessions: StateFlow<List<SessionEntity>> = combine(
        allSessions,
        selectedHeatmapDate
    ) { sessions, date ->
        if (date == null) emptyList() else {
            val dateStr = date.format(TimeLoomAlgorithms.DATE_FORMATTER)
            sessions.filter { it.dateString == dateStr }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly Report via Algorithm 5
    val weeklyReport: StateFlow<TimeLoomAlgorithms.WeeklyReport> = combine(
        allSessions,
        allTasks
    ) { sessions, tasks ->
        TimeLoomAlgorithms.generateWeeklyReport(sessions, tasks, LocalDate.now())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TimeLoomAlgorithms.generateWeeklyReport(emptyList(), emptyList(), LocalDate.now())
    )

    // Productivity Score via Algorithm 6
    val productivityScore: StateFlow<TimeLoomAlgorithms.ProductivityScoreResult> = combine(
        allTasks,
        allSessions,
        userPreferences
    ) { tasks, sessions, prefs ->
        TimeLoomAlgorithms.calculateProductivityScore(
            tasks = tasks,
            sessions = sessions,
            dailyGoalMinutes = prefs.dailyGoalMinutes,
            todayDate = LocalDate.now()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TimeLoomAlgorithms.calculateProductivityScore(emptyList(), emptyList(), 120, LocalDate.now())
    )

    // Today's Tracked Time
    val todayTrackedMinutes: StateFlow<Int> = combine(allSessions) { (sessions) ->
        val todayStr = LocalDate.now().format(TimeLoomAlgorithms.DATE_FORMATTER)
        sessions.filter { it.dateString == todayStr }.sumOf { it.durationSeconds } / 60
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Resume ticker if restored timer was already active
        val restored = _activeTimerState.value
        if (restored.isRunning) {
            updateElapsedCalculation()
            if (!restored.isPaused) {
                startTimerTicker()
            }
        }
    }

    // -------------------------------------------------------------
    // Navigation actions
    // -------------------------------------------------------------
    fun setTab(tab: TimeLoomTab) {
        _currentTab.value = tab
    }

    fun openSettings() {
        _showSettings.value = true
    }

    fun closeSettings() {
        _showSettings.value = false
    }

    fun openVivaScreen() {
        _showVivaScreen.value = true
    }

    fun closeVivaScreen() {
        _showVivaScreen.value = false
    }

    fun openScoreModal() {
        _showScoreModal.value = true
    }

    fun closeScoreModal() {
        _showScoreModal.value = false
    }

    fun openAddTaskDialog() {
        _editingTask.value = null
        _isAddEditDialogOpen.value = true
    }

    fun openEditTaskDialog(task: TaskEntity) {
        _editingTask.value = task
        _isAddEditDialogOpen.value = true
    }

    fun closeAddEditDialog() {
        _editingTask.value = null
        _isAddEditDialogOpen.value = false
    }

    // -------------------------------------------------------------
    // Task CRUD
    // -------------------------------------------------------------
    fun saveTask(
        id: Long,
        title: String,
        description: String,
        category: String,
        priority: String,
        dueDate: String,
        expectedDuration: Int
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                val newTask = TaskEntity(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    dueDate = dueDate,
                    expectedDuration = expectedDuration,
                    status = TaskStatus.PENDING.name
                )
                repository.addTask(newTask)
            } else {
                val existing = _editingTask.value
                val updated = existing?.copy(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    dueDate = dueDate,
                    expectedDuration = expectedDuration,
                    updatedAt = System.currentTimeMillis()
                ) ?: TaskEntity(
                    id = id,
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    dueDate = dueDate,
                    expectedDuration = expectedDuration,
                    status = TaskStatus.PENDING.name
                )
                repository.updateTask(updated)
            }
            closeAddEditDialog()
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }

    fun toggleTaskComplete(task: TaskEntity) {
        viewModelScope.launch {
            val newStatus = if (task.status.equals(TaskStatus.COMPLETED.name, ignoreCase = true)) {
                TaskStatus.PENDING
            } else {
                TaskStatus.COMPLETED
            }
            repository.updateTaskStatus(task.id, newStatus)
        }
    }

    fun updateTaskStatus(id: Long, status: TaskStatus) {
        viewModelScope.launch {
            repository.updateTaskStatus(id, status)
        }
    }

    // -------------------------------------------------------------
    // Timer Operations (Algorithms 1 + Persistent State)
    // -------------------------------------------------------------
    fun startTimer(taskId: Long?, title: String, category: String) {
        val now = System.currentTimeMillis()
        val newState = ActiveTimerState(
            isRunning = true,
            isPaused = false,
            taskId = taskId,
            taskTitle = title,
            category = category,
            startTimestamp = now,
            accumulatedPauseMillis = 0L,
            pauseStartTimestamp = 0L
        )
        _activeTimerState.value = newState
        repository.saveActiveTimer(newState)
        _timerDisplaySeconds.value = 0
        startTimerTicker()
    }

    fun pauseTimer() {
        val current = _activeTimerState.value
        if (!current.isRunning || current.isPaused) return

        val now = System.currentTimeMillis()
        val updated = current.copy(
            isPaused = true,
            pauseStartTimestamp = now
        )
        _activeTimerState.value = updated
        repository.saveActiveTimer(updated)
        timerJob?.cancel()
        updateElapsedCalculation()
    }

    fun resumeTimer() {
        val current = _activeTimerState.value
        if (!current.isRunning || !current.isPaused) return

        val now = System.currentTimeMillis()
        val pauseDuration = if (current.pauseStartTimestamp > 0) now - current.pauseStartTimestamp else 0L
        val updated = current.copy(
            isPaused = false,
            accumulatedPauseMillis = current.accumulatedPauseMillis + pauseDuration,
            pauseStartTimestamp = 0L
        )
        _activeTimerState.value = updated
        repository.saveActiveTimer(updated)
        startTimerTicker()
    }

    fun stopTimer() {
        val current = _activeTimerState.value
        if (!current.isRunning) return

        val now = System.currentTimeMillis()
        var totalPause = current.accumulatedPauseMillis
        if (current.isPaused && current.pauseStartTimestamp > 0) {
            totalPause += (now - current.pauseStartTimestamp)
        }

        viewModelScope.launch {
            repository.recordCompletedSession(
                taskId = current.taskId,
                activityName = current.taskTitle,
                category = current.category,
                startTime = current.startTimestamp,
                endTime = now,
                pausedMillis = totalPause
            )
            repository.clearActiveTimer()
            timerJob?.cancel()
            _activeTimerState.value = ActiveTimerState()
            _timerDisplaySeconds.value = 0
        }
    }

    private fun startTimerTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                updateElapsedCalculation()
                delay(1000L)
            }
        }
    }

    private fun updateElapsedCalculation() {
        val current = _activeTimerState.value
        if (!current.isRunning || current.startTimestamp == 0L) {
            _timerDisplaySeconds.value = 0
            return
        }

        val now = System.currentTimeMillis()
        val pauseDuration = if (current.isPaused && current.pauseStartTimestamp > 0) {
            current.accumulatedPauseMillis + (now - current.pauseStartTimestamp)
        } else {
            current.accumulatedPauseMillis
        }

        val seconds = TimeLoomAlgorithms.calculateActiveDuration(
            startTimeMillis = current.startTimestamp,
            endTimeMillis = now,
            pausedDurationMillis = pauseDuration
        )
        _timerDisplaySeconds.value = seconds
    }

    // -------------------------------------------------------------
    // Heatmap Navigation
    // -------------------------------------------------------------
    fun previousMonth() {
        heatmapMonthOffset.value -= 1
    }

    fun nextMonth() {
        heatmapMonthOffset.value += 1
    }

    fun resetToCurrentMonth() {
        heatmapMonthOffset.value = 0
    }

    fun selectHeatmapDate(date: LocalDate) {
        selectedHeatmapDate.value = date
    }

    // -------------------------------------------------------------
    // Preferences & Reset
    // -------------------------------------------------------------
    fun updatePreferences(name: String, email: String, dailyGoalMinutes: Int, vibration: Boolean) {
        val updated = _userPreferences.value.copy(
            name = name,
            email = email,
            dailyGoalMinutes = dailyGoalMinutes,
            vibrationEnabled = vibration
        )
        _userPreferences.value = updated
        repository.saveUserPreferences(updated)
    }

    fun resetDatabaseToAcademicDemo() {
        viewModelScope.launch {
            repository.resetAllData()
            _activeTimerState.value = repository.loadActiveTimer()
            _timerDisplaySeconds.value = 0
            timerJob?.cancel()
        }
    }
}
