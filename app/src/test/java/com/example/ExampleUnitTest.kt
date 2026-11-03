package com.example

import com.example.algorithms.TimeLoomAlgorithms
import com.example.data.model.SessionEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

    @Test
    fun testAlgorithm1_durationCalculation() {
        // 10000ms start, 70000ms end (60s gross), 10000ms paused = 50s net
        val seconds = TimeLoomAlgorithms.calculateActiveDuration(
            startTimeMillis = 10000L,
            endTimeMillis = 70000L,
            pausedDurationMillis = 10000L
        )
        assertEquals(50, seconds)
    }

    @Test
    fun testAlgorithm2_taskFiltering() {
        val tasks = listOf(
            TaskEntity(id = 1, title = "Graph BFS Lab", category = "Coding", status = "IN_PROGRESS"),
            TaskEntity(id = 2, title = "OS Semaphore", category = "Coding", status = "COMPLETED"),
            TaskEntity(id = 3, title = "DB Normalization", category = "Study", status = "PENDING")
        )

        val filtered = TimeLoomAlgorithms.filterTasks(
            tasks = tasks,
            query = "Graph",
            category = "Coding",
            status = "IN_PROGRESS"
        )
        assertEquals(1, filtered.size)
        assertEquals("Graph BFS Lab", filtered[0].title)
    }

    @Test
    fun testAlgorithm3_dailyAggregation() {
        val sessions = listOf(
            SessionEntity(id = 1, activityName = "S1", startTime = 0, endTime = 0, durationSeconds = 1800, dateString = "2026-11-02"),
            SessionEntity(id = 2, activityName = "S2", startTime = 0, endTime = 0, durationSeconds = 3600, dateString = "2026-11-02"),
            SessionEntity(id = 3, activityName = "S3", startTime = 0, endTime = 0, durationSeconds = 1200, dateString = "2026-11-01")
        )
        val dailyMap = TimeLoomAlgorithms.aggregateDailyMinutes(sessions)
        assertEquals(90, dailyMap["2026-11-02"])
        assertEquals(20, dailyMap["2026-11-01"])
    }

    @Test
    fun testAlgorithm4_heatmapIntensityThresholds() {
        assertEquals(0, TimeLoomAlgorithms.getIntensityLevel(0))
        assertEquals(1, TimeLoomAlgorithms.getIntensityLevel(25))
        assertEquals(2, TimeLoomAlgorithms.getIntensityLevel(45))
        assertEquals(3, TimeLoomAlgorithms.getIntensityLevel(90))
        assertEquals(4, TimeLoomAlgorithms.getIntensityLevel(150))
    }

    @Test
    fun testAlgorithm6_productivityScoreCalculation() {
        val tasks = listOf(
            TaskEntity(id = 1, title = "T1", status = TaskStatus.COMPLETED.name),
            TaskEntity(id = 2, title = "T2", status = TaskStatus.COMPLETED.name),
            TaskEntity(id = 3, title = "T3", status = TaskStatus.PENDING.name)
        )
        val todayStr = LocalDate.now().format(TimeLoomAlgorithms.DATE_FORMATTER)
        val sessions = listOf(
            SessionEntity(id = 1, activityName = "S1", startTime = 0, endTime = 0, durationSeconds = 7200, dateString = todayStr) // 120 mins
        )

        val result = TimeLoomAlgorithms.calculateProductivityScore(
            tasks = tasks,
            sessions = sessions,
            dailyGoalMinutes = 120,
            todayDate = LocalDate.now()
        )
        // Task score: 2/3 * 100 = 67
        // Focus score: 120/120 * 100 = 100
        // Consistency: 1/7 * 100 = 14
        // Overall: 0.4*67 (26.8) + 0.4*100 (40) + 0.2*14 (2.8) = 69.6 ~ 70
        assertTrue(result.overallScore in 65..75)
    }
}
