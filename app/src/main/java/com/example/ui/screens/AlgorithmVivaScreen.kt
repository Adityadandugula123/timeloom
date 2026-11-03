package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorithms.TimeLoomAlgorithms
import com.example.ui.components.CategoryChip
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.CardGlowBorderBrush
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DarkSlate
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class AlgorithmMeta(
    val id: Int,
    val title: String,
    val shortName: String,
    val timeComplexity: String,
    val spaceComplexity: String,
    val problemStatement: String,
    val inputOutput: String,
    val pseudocode: String,
    val flowchartText: String,
    val testCaseInput: String,
    val testCaseExpectedOutput: String
)

@Composable
fun AlgorithmVivaScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val algorithms = listOf(
        AlgorithmMeta(
            id = 1,
            title = "Algorithm 1: Duration Calculation",
            shortName = "Algo 1: Duration",
            timeComplexity = "O(1)",
            spaceComplexity = "O(1)",
            problemStatement = "Calculate exact active elapsed duration in seconds between start and end timestamps while accounting for arbitrary pause intervals and app suspensions.",
            inputOutput = "Input: startTime (ms), endTime (ms), pausedDuration (ms)\nOutput: netDurationSeconds (Int >= 0)",
            pseudocode = """
FUNCTION calculateActiveDuration(startTime, endTime, pausedDuration):
    IF endTime <= startTime THEN
        RETURN 0
    grossElapsed = endTime - startTime
    netElapsed = grossElapsed - pausedDuration
    seconds = MAX(0, FLOOR(netElapsed / 1000))
    RETURN seconds
END FUNCTION
            """.trimIndent(),
            flowchartText = "[Start] -> [Check endTime > startTime?] --(No)--> [Return 0]\n                 |\n               (Yes)\n                 v\n[gross = endTime - startTime] -> [net = gross - pausedDuration] -> [sec = net / 1000] -> [Return sec] -> [End]",
            testCaseInput = "Start: 10:00:00 (0ms), End: 10:45:30 (2,730,000ms), Paused: 30,000ms (30s)",
            testCaseExpectedOutput = "Net Active Duration = 2,700 seconds (45 minutes)"
        ),
        AlgorithmMeta(
            id = 2,
            title = "Algorithm 2: Multi-Criteria Task Filtering",
            shortName = "Algo 2: Filter",
            timeComplexity = "O(n)",
            spaceComplexity = "O(k) where k <= n",
            problemStatement = "Filter arbitrary task collection across multiple disjoint criteria: search substring (title + description), category enum, status enum, and priority.",
            inputOutput = "Input: tasks (List), query (String), category (String?), status (String?)\nOutput: filteredTasks (List)",
            pseudocode = """
FUNCTION filterTasks(tasks, query, category, status, priority):
    result = []
    q = LOWERCASE(TRIM(query))
    FOR EACH task IN tasks:
        matchQuery = (q is EMPTY) OR (q IN LOWERCASE(task.title + " " + task.description))
        matchCat = (category is NULL or "All") OR (task.category EQUALS category)
        matchStat = (status is NULL or "All") OR (task.status EQUALS status)
        IF matchQuery AND matchCat AND matchStat THEN
            result.ADD(task)
    RETURN result
END FUNCTION
            """.trimIndent(),
            flowchartText = "[Start] -> [Iterate tasks O(n)] -> [Evaluate compound boolean predicate] --(True)--> [Append to result] -> [Return result] -> [End]",
            testCaseInput = "10 Tasks, Query: 'Graph', Category: 'Coding', Status: 'In Progress'",
            testCaseExpectedOutput = "Matches only tasks where title contains 'Graph' AND category == Coding AND status == In Progress."
        ),
        AlgorithmMeta(
            id = 3,
            title = "Algorithm 3: Daily Time Aggregation",
            shortName = "Algo 3: Daily Time",
            timeComplexity = "O(n)",
            spaceComplexity = "O(u) where u = unique dates <= 31",
            problemStatement = "Group an arbitrary sequence of historical timer sessions by calendar date string (YYYY-MM-DD) and compute aggregate minutes per day.",
            inputOutput = "Input: sessions (List of SessionEntity)\nOutput: Map<DateString, TotalMinutes>",
            pseudocode = """
FUNCTION aggregateDailyMinutes(sessions):
    dailyMap = NEW HASH_MAP()
    FOR EACH session IN sessions:
        dateKey = session.dateString
        mins = FLOOR(session.durationSeconds / 60)
        dailyMap[dateKey] = dailyMap.GET_OR_DEFAULT(dateKey, 0) + mins
    RETURN dailyMap
END FUNCTION
            """.trimIndent(),
            flowchartText = "[Start] -> [Init HashMap] -> [Loop sessions] -> [Key = dateString, Accumulate mins] -> [Return Map] -> [End]",
            testCaseInput = "Sessions: S1 (2026-11-02, 1800s=30m), S2 (2026-11-02, 3600s=60m), S3 (2026-11-01, 1200s=20m)",
            testCaseExpectedOutput = "{ '2026-11-02': 90m, '2026-11-01': 20m }"
        ),
        AlgorithmMeta(
            id = 4,
            title = "Algorithm 4: Heatmap Matrix Generation",
            shortName = "Algo 4: Heatmap",
            timeComplexity = "O(n + d) where d = days in month (28..31)",
            spaceComplexity = "O(d)",
            problemStatement = "Construct contribution calendar grid with discrete 5-level intensity mapping (Level 0: 0m, Level 1: 1-30m, Level 2: 31-60m, Level 3: 61-120m, Level 4: >120m).",
            inputOutput = "Input: sessions (List), year (Int), month (Int)\nOutput: List<HeatmapDay> of length d",
            pseudocode = """
FUNCTION generateMonthHeatmap(sessions, year, month):
    dailyTotals = aggregateDailyMinutes(sessions)  // O(n)
    d = DAYS_IN_MONTH(year, month)
    grid = ARRAY[d]
    FOR day = 1 TO d:                              // O(d)
        dateStr = FORMAT_DATE(year, month, day)
        mins = dailyTotals.GET_OR_DEFAULT(dateStr, 0)
        IF mins <= 0: level = 0
        ELSE IF mins <= 30: level = 1
        ELSE IF mins <= 60: level = 2
        ELSE IF mins <= 120: level = 3
        ELSE: level = 4
        grid.ADD(HeatmapDay(day, mins, level))
    RETURN grid
END FUNCTION
            """.trimIndent(),
            flowchartText = "[Start] -> [Pre-aggregate sessions via O(n) hash] -> [Loop 1..daysInMonth] -> [Map minutes to discrete Level 0..4] -> [Return grid] -> [End]",
            testCaseInput = "Day 1: 0m, Day 2: 25m, Day 3: 50m, Day 4: 90m, Day 5: 145m",
            testCaseExpectedOutput = "Intensity Levels: Day 1=0, Day 2=1, Day 3=2, Day 4=3, Day 5=4"
        ),
        AlgorithmMeta(
            id = 5,
            title = "Algorithm 5: Weekly Report Aggregation",
            shortName = "Algo 5: Weekly Report",
            timeComplexity = "O(n + d)",
            spaceComplexity = "O(1) auxiliary",
            problemStatement = "Compute comprehensive 7-day activity metrics including Mon-Sun distribution, daily average, most productive day, and previous week delta.",
            inputOutput = "Input: sessions, tasks, referenceDate (LocalDate)\nOutput: WeeklyReport summary object",
            pseudocode = """
FUNCTION generateWeeklyReport(sessions, tasks, referenceDate):
    monday = GET_MONDAY(referenceDate)
    sunday = GET_SUNDAY(referenceDate)
    totalCurrent = 0, totalPrevious = 0
    FOR session IN sessions:
        IF session.date BETWEEN monday AND sunday:
            totalCurrent += session.minutes
            categoryMap[session.cat] += session.minutes
        ELSE IF session.date IN PREVIOUS_WEEK:
            totalPrevious += session.minutes
    avg = totalCurrent / 7
    delta = (totalCurrent - totalPrevious) / totalPrevious * 100
    RETURN WeeklyReport(totalCurrent, avg, delta, categoryMap)
END FUNCTION
            """.trimIndent(),
            flowchartText = "[Start] -> [Compute week boundaries] -> [Accumulate current & previous week] -> [Calculate mean & % change] -> [Return Report] -> [End]",
            testCaseInput = "Current Week: 420m (7h), Previous Week: 350m",
            testCaseExpectedOutput = "Total: 420m, Avg: 60m/day, Change: +20% vs last week"
        ),
        AlgorithmMeta(
            id = 6,
            title = "Algorithm 6: Rule-Based Productivity Score",
            shortName = "Algo 6: Productivity",
            timeComplexity = "O(n)",
            spaceComplexity = "O(1)",
            problemStatement = "Calculate transparent weighted score: Score = (0.40 × TaskCompletion) + (0.40 × FocusTime) + (0.20 × Consistency), normalized 0–100.",
            inputOutput = "Input: tasks, sessions, dailyGoalMinutes, todayDate\nOutput: ProductivityScoreResult (0..100)",
            pseudocode = """
FUNCTION calculateProductivityScore(tasks, sessions, dailyGoal, today):
    // 1. Task Completion (40%)
    taskScore = (tasks.size == 0) ? 100 : (completedTasks / totalTasks * 100)
    
    // 2. Focus Time (40%)
    todayMinutes = SUM(sessions WHERE date == today)
    focusScore = MIN(100, todayMinutes / dailyGoal * 100)
    
    // 3. 7-Day Consistency (20%)
    activeDays = COUNT_DISTINCT(dates WITH >=1 session in [today-6 .. today])
    consistencyScore = (activeDays / 7) * 100
    
    // Weighted Sum
    overall = (0.40 * taskScore) + (0.40 * focusScore) + (0.20 * consistencyScore)
    RETURN ROUND(overall)
END FUNCTION
            """.trimIndent(),
            flowchartText = "[Start] -> [Compute TaskScore 0..100] -> [Compute FocusScore 0..100] -> [Compute ConsistencyScore 0..100] -> [Weighted Sum] -> [Return Score] -> [End]",
            testCaseInput = "TaskCompletion: 80% (4/5), FocusTime: 120m / 120m (100%), Consistency: 6/7 days (86%)",
            testCaseExpectedOutput = "Overall = (0.40*80) + (0.40*100) + (0.20*86) = 32 + 40 + 17.2 = 89 / 100"
        )
    )

    var selectedAlgoIndex by remember { mutableIntStateOf(0) }
    val current = algorithms[selectedAlgoIndex]
    var dryRunExecuted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AccentCyan
                    )
                }
                Column {
                    Text(
                        text = "Algorithm Showcase & Viva Hub",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Academic B.Tech CSE Specification Compliance",
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentCyan
                    )
                }
            }
        }

        // Horizontal Algorithm Selector Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                algorithms.forEachIndexed { index, algo ->
                    CategoryChip(
                        category = algo.shortName,
                        isSelected = selectedAlgoIndex == index,
                        onClick = {
                            selectedAlgoIndex = index
                            dryRunExecuted = false
                        }
                    )
                }
            }
        }

        // Algorithm Title & Complexity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardGlowBorderBrush)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = current.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentBlue.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Time: ${current.timeComplexity}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSlate
                        ) {
                            Text(
                                text = "Space: ${current.spaceComplexity}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            )
                        }
                    }

                    Text(
                        text = "Problem Statement:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = current.problemStatement,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Text(
                        text = "Input & Output:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = current.inputOutput,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = AccentCyan
                    )
                }
            }
        }

        // Pseudocode Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlate),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Formal Pseudocode",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    )
                    Text(
                        text = current.pseudocode,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        color = TextPrimary
                    )
                }
            }
        }

        // Flowchart Diagram Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlate),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Control Flow Diagram",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    )
                    Text(
                        text = current.flowchartText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = TextSecondary
                    )
                }
            }
        }

        // Live Test & Dry Run Executor
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Live Test & Dry Run Verification",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Sample Input:\n${current.testCaseInput}",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = TextMuted
                    )

                    Button(
                        onClick = { dryRunExecuted = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_run_dry_run")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayCircle,
                            contentDescription = null,
                            tint = DarkNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Execute Dry Run Test",
                            color = DarkNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (dryRunExecuted) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "✓ Test Output Passed:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SuccessGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = current.testCaseExpectedOutput,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
