package com.example.algorithms

import com.example.data.model.SessionEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Time Loom Algorithmic Engine
 * Academic B.Tech CSE Implementation
 *
 * Implements the 6 primary algorithms specified in the Project Specification:
 * 1. Duration Calculation - O(1)
 * 2. Task & Activity Filtering - O(n)
 * 3. Daily Time Aggregation - O(n)
 * 4. Heatmap Matrix Generation - O(n + d)
 * 5. Weekly Report Aggregation - O(n + d)
 * 6. Rule-based Productivity Score Calculation - O(n)
 */
object TimeLoomAlgorithms {

    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // =========================================================================
    // ALGORITHM 1: DURATION CALCULATION
    // Time Complexity: O(1) | Space Complexity: O(1)
    // =========================================================================
    /**
     * Calculates actual active duration in seconds by subtracting accumulated pause duration
     * from the raw interval between startTime and endTime.
     *
     * @param startTimeMillis Epoch timestamp when timer started
     * @param endTimeMillis Epoch timestamp when timer stopped
     * @param pausedDurationMillis Total milliseconds elapsed during paused states
     * @return Net active duration in seconds (non-negative)
     */
    fun calculateActiveDuration(
        startTimeMillis: Long,
        endTimeMillis: Long,
        pausedDurationMillis: Long = 0L
    ): Int {
        if (endTimeMillis <= startTimeMillis) return 0
        val grossElapsed = endTimeMillis - startTimeMillis
        val netElapsed = grossElapsed - pausedDurationMillis
        val seconds = (netElapsed / 1000L).coerceAtLeast(0L)
        return seconds.toInt()
    }

    // =========================================================================
    // ALGORITHM 2: TASK FILTERING
    // Time Complexity: O(n) | Space Complexity: O(k) where k <= n
    // =========================================================================
    /**
     * Filters tasks based on search text, category, status, and priority.
     * Employs linear scan with short-circuit evaluation.
     */
    fun filterTasks(
        tasks: List<TaskEntity>,
        query: String = "",
        category: String? = null,
        status: String? = null,
        priority: String? = null
    ): List<TaskEntity> {
        val trimmedQuery = query.trim().lowercase()
        return tasks.filter { task ->
            val matchesQuery = if (trimmedQuery.isEmpty()) true else {
                task.title.lowercase().contains(trimmedQuery) ||
                        task.description.lowercase().contains(trimmedQuery)
            }
            val matchesCategory = if (category.isNullOrEmpty() || category.equals("All", ignoreCase = true)) true else {
                task.category.equals(category, ignoreCase = true)
            }
            val matchesStatus = if (status.isNullOrEmpty() || status.equals("All", ignoreCase = true)) true else {
                task.status.equals(status, ignoreCase = true)
            }
            val matchesPriority = if (priority.isNullOrEmpty() || priority.equals("All", ignoreCase = true)) true else {
                task.priority.equals(priority, ignoreCase = true)
            }
            matchesQuery && matchesCategory && matchesStatus && matchesPriority
        }
    }

    // =========================================================================
    // ALGORITHM 3: DAILY TIME AGGREGATION
    // Time Complexity: O(n) | Space Complexity: O(u) where u is unique days
    // =========================================================================
    /**
     * Groups completed sessions by calendar date and calculates total tracked minutes.
     *
     * @param sessions List of user sessions
     * @return Map of DateString (YYYY-MM-DD) to total tracked minutes
     */
    fun aggregateDailyMinutes(sessions: List<SessionEntity>): Map<String, Int> {
        val dailyMap = mutableMapOf<String, Int>()
        for (session in sessions) {
            val date = session.dateString
            val sessionMinutes = session.durationSeconds / 60
            val currentTotal = dailyMap.getOrDefault(date, 0)
            dailyMap[date] = currentTotal + sessionMinutes
        }
        return dailyMap
    }

    // =========================================================================
    // ALGORITHM 4: HEATMAP GENERATION
    // Time Complexity: O(n + d) | Space Complexity: O(d)
    // =========================================================================
    data class HeatmapDay(
        val date: LocalDate,
        val dateString: String,
        val dayOfMonth: Int,
        val trackedMinutes: Int,
        val intensityLevel: Int // 0 to 4
    )

    /**
     * Assigns contribution intensity level based on defined project thresholds:
     * Level 0: 0 minutes (no activity)
     * Level 1: 1 - 30 minutes
     * Level 2: 31 - 60 minutes
     * Level 3: 61 - 120 minutes
     * Level 4: > 120 minutes
     */
    fun getIntensityLevel(minutes: Int): Int {
        return when {
            minutes <= 0 -> 0
            minutes in 1..30 -> 1
            minutes in 31..60 -> 2
            minutes in 61..120 -> 3
            else -> 4
        }
    }

    /**
     * Generates a complete month grid of HeatmapDay objects with intensities.
     *
     * @param sessions All recorded sessions
     * @param year Current year
     * @param month Current month (1-12)
     */
    fun generateMonthHeatmap(
        sessions: List<SessionEntity>,
        year: Int,
        month: Int
    ): List<HeatmapDay> {
        // Step 1: Precompute daily totals via hash map - O(n)
        val dailyTotals = aggregateDailyMinutes(sessions)

        // Step 2: Iterate over all days in the specified month - O(d)
        val firstDay = LocalDate.of(year, month, 1)
        val daysInMonth = firstDay.lengthOfMonth()
        val result = ArrayList<HeatmapDay>(daysInMonth)

        for (day in 1..daysInMonth) {
            val date = LocalDate.of(year, month, day)
            val dateStr = date.format(DATE_FORMATTER)
            val minutes = dailyTotals[dateStr] ?: 0
            val level = getIntensityLevel(minutes)
            result.add(HeatmapDay(date, dateStr, day, minutes, level))
        }
        return result
    }

    // =========================================================================
    // ALGORITHM 5: WEEKLY REPORT GENERATION
    // Time Complexity: O(n + d) | Space Complexity: O(1)
    // =========================================================================
    data class DayActivity(
        val dayOfWeek: DayOfWeek,
        val date: LocalDate,
        val trackedMinutes: Int
    )

    data class WeeklyReport(
        val weekStartDate: LocalDate,
        val weekEndDate: LocalDate,
        val totalTrackedMinutes: Int,
        val dailyActivities: List<DayActivity>, // Monday to Sunday
        val completedTasksCount: Int,
        val pendingTasksCount: Int,
        val categoryBreakdown: Map<String, Int>, // Category -> minutes
        val mostActiveDay: DayOfWeek?,
        val averageDailyMinutes: Int,
        val previousWeekComparisonPercent: Int // e.g. +15% or -10%
    )

    fun generateWeeklyReport(
        sessions: List<SessionEntity>,
        tasks: List<TaskEntity>,
        referenceDate: LocalDate = LocalDate.now()
    ): WeeklyReport {
        // Determine start (Monday) and end (Sunday) of reference week
        val monday = referenceDate.with(DayOfWeek.MONDAY)
        val sunday = referenceDate.with(DayOfWeek.SUNDAY)
        val prevMonday = monday.minusWeeks(1)
        val prevSunday = sunday.minusWeeks(1)

        val mondayStr = monday.format(DATE_FORMATTER)
        val sundayStr = sunday.format(DATE_FORMATTER)
        val prevMondayStr = prevMonday.format(DATE_FORMATTER)
        val prevSundayStr = prevSunday.format(DATE_FORMATTER)

        var totalMinutesCurrent = 0
        var totalMinutesPrevious = 0
        val categoryMap = mutableMapOf<String, Int>()
        val dayMinutesMap = mutableMapOf<LocalDate, Int>()

        // Initialize all 7 days with 0
        for (i in 0..6) {
            dayMinutesMap[monday.plusDays(i.toLong())] = 0
        }

        for (session in sessions) {
            val minutes = session.durationSeconds / 60
            val dateStr = session.dateString

            // Check if falls in current week
            if (dateStr in mondayStr..sundayStr) {
                totalMinutesCurrent += minutes
                val cat = session.category.ifBlank { "Other" }
                categoryMap[cat] = categoryMap.getOrDefault(cat, 0) + minutes

                try {
                    val sDate = LocalDate.parse(dateStr, DATE_FORMATTER)
                    dayMinutesMap[sDate] = (dayMinutesMap[sDate] ?: 0) + minutes
                } catch (_: Exception) {}
            } else if (dateStr in prevMondayStr..prevSundayStr) {
                totalMinutesPrevious += minutes
            }
        }

        val dailyList = (0..6).map { i ->
            val d = monday.plusDays(i.toLong())
            DayActivity(d.dayOfWeek, d, dayMinutesMap[d] ?: 0)
        }

        val mostActive = dailyList.maxByOrNull { it.trackedMinutes }?.takeIf { it.trackedMinutes > 0 }?.dayOfWeek
        val avgDaily = totalMinutesCurrent / 7

        val comparisonPercent = if (totalMinutesPrevious == 0) {
            if (totalMinutesCurrent > 0) 100 else 0
        } else {
            (((totalMinutesCurrent - totalMinutesPrevious).toFloat() / totalMinutesPrevious) * 100).roundToInt()
        }

        val completedCount = tasks.count { it.status.equals(TaskStatus.COMPLETED.name, ignoreCase = true) }
        val pendingCount = tasks.count { !it.status.equals(TaskStatus.COMPLETED.name, ignoreCase = true) }

        return WeeklyReport(
            weekStartDate = monday,
            weekEndDate = sunday,
            totalTrackedMinutes = totalMinutesCurrent,
            dailyActivities = dailyList,
            completedTasksCount = completedCount,
            pendingTasksCount = pendingCount,
            categoryBreakdown = categoryMap,
            mostActiveDay = mostActive,
            averageDailyMinutes = avgDaily,
            previousWeekComparisonPercent = comparisonPercent
        )
    }

    // =========================================================================
    // ALGORITHM 6: PRODUCTIVITY SCORE CALCULATION
    // Time Complexity: O(n) | Space Complexity: O(1)
    // =========================================================================
    data class ProductivityScoreResult(
        val overallScore: Int, // 0 - 100
        val taskCompletionScore: Int, // 0 - 100 (40% weight)
        val focusTimeScore: Int, // 0 - 100 (40% weight)
        val consistencyScore: Int, // 0 - 100 (20% weight)
        val explanation: String
    )

    /**
     * Calculates transparent, rule-based productivity score:
     * Productivity Score = (0.40 * TaskCompletion) + (0.40 * FocusTime) + (0.20 * Consistency)
     *
     * @param tasks List of all user tasks
     * @param sessions List of all user sessions
     * @param dailyGoalMinutes User's target daily tracked time
     * @param todayDate Current calendar date
     */
    fun calculateProductivityScore(
        tasks: List<TaskEntity>,
        sessions: List<SessionEntity>,
        dailyGoalMinutes: Int = 120,
        todayDate: LocalDate = LocalDate.now()
    ): ProductivityScoreResult {
        // Component 1: Task Completion Score (0-100)
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.status.equals(TaskStatus.COMPLETED.name, ignoreCase = true) }
        val taskCompletionScore = if (totalTasks == 0) {
            100 // Safe empty case: No overdue tasks
        } else {
            ((completedTasks.toFloat() / totalTasks) * 100).roundToInt().coerceIn(0, 100)
        }

        // Component 2: Focus Time Score (0-100)
        val todayStr = todayDate.format(DATE_FORMATTER)
        val todayTrackedMinutes = sessions
            .filter { it.dateString == todayStr }
            .sumOf { it.durationSeconds } / 60

        val focusTimeScore = if (dailyGoalMinutes <= 0) {
            100
        } else {
            min(100, ((todayTrackedMinutes.toFloat() / dailyGoalMinutes) * 100).roundToInt())
        }

        // Component 3: Consistency Score (0-100)
        // Percentage of days in the last 7 days with >= 1 tracked session
        val last7Days = (0..6).map { todayDate.minusDays(it.toLong()).format(DATE_FORMATTER) }.toSet()
        val activeDaysCount = sessions
            .map { it.dateString }
            .filter { it in last7Days }
            .distinct()
            .count()
        val consistencyScore = ((activeDaysCount.toFloat() / 7) * 100).roundToInt().coerceIn(0, 100)

        // Weighted Overall Score
        val weightedScore = (0.40f * taskCompletionScore) +
                (0.40f * focusTimeScore) +
                (0.20f * consistencyScore)
        val overall = weightedScore.roundToInt().coerceIn(0, 100)

        val explanation = when {
            overall >= 80 -> "Exceptional productivity! Balanced focus, consistent daily check-ins, and high task completion."
            overall >= 60 -> "Strong momentum. Keep your daily focus goal within reach to unlock peak consistency."
            overall >= 40 -> "Moderate output. Increasing focus session length will significantly improve your score."
            else -> "Early progress. Start a quick 25-minute timer session today to build momentum."
        }

        return ProductivityScoreResult(
            overallScore = overall,
            taskCompletionScore = taskCompletionScore,
            focusTimeScore = focusTimeScore,
            consistencyScore = consistencyScore,
            explanation = explanation
        )
    }
}
