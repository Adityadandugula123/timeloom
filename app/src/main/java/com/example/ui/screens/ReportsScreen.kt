package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorithms.TimeLoomAlgorithms
import com.example.ui.components.getCategoryColor
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.CardGlowBorderBrush
import com.example.ui.theme.CyanBlueGlowBrush
import com.example.ui.theme.DangerRose
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DarkSlate
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.TimeLoomViewModel
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: TimeLoomViewModel,
    modifier: Modifier = Modifier
) {
    val weeklyReport by viewModel.weeklyReport.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()

    var selectedReportTab by remember { mutableIntStateOf(0) } // 0 = Weekly, 1 = Monthly

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Productivity Analytics",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Algorithm 5: Temporal Aggregations & Distribution Metrics",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans),
                        color = AccentCyan
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentCyan.copy(alpha = 0.15f))
                        .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Assessment,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Period Toggle Tabs (Weekly vs Monthly)
        item {
            TabRow(
                selectedTabIndex = selectedReportTab,
                containerColor = DarkSlate,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedReportTab]),
                        color = AccentCyan
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedReportTab == 0,
                    onClick = { selectedReportTab = 0 },
                    text = {
                        Text(
                            text = "Weekly Breakdown",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = if (selectedReportTab == 0) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedReportTab == 0) AccentCyan else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedReportTab == 1,
                    onClick = { selectedReportTab = 1 },
                    text = {
                        Text(
                            text = "Monthly Overview",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = if (selectedReportTab == 1) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedReportTab == 1) AccentCyan else TextSecondary
                        )
                    }
                )
            }
        }

        if (selectedReportTab == 0) {
            item {
                WeeklyOverviewCard(report = weeklyReport)
            }

            item {
                WeeklyBarChartCard(report = weeklyReport)
            }

            item {
                CategoryDistributionCard(
                    categoryMap = weeklyReport.categoryBreakdown,
                    totalMinutes = weeklyReport.totalTrackedMinutes
                )
            }

            item {
                TaskStatusSummaryCard(
                    completed = weeklyReport.completedTasksCount,
                    pending = weeklyReport.pendingTasksCount
                )
            }
        } else {
            val currentMonthTotalMinutes = allSessions.sumOf { it.durationSeconds } / 60
            val completedCount = allTasks.count { it.status == "COMPLETED" }
            val totalCount = allTasks.size

            item {
                MonthlyOverviewCard(
                    totalMinutes = currentMonthTotalMinutes,
                    completedTasks = completedCount,
                    totalTasks = totalCount,
                    sessionsCount = allSessions.size
                )
            }

            item {
                val monthCategoryMap = mutableMapOf<String, Int>()
                allSessions.forEach { s ->
                    val cat = s.category.ifBlank { "Other" }
                    monthCategoryMap[cat] = monthCategoryMap.getOrDefault(cat, 0) + (s.durationSeconds / 60)
                }
                CategoryDistributionCard(
                    categoryMap = monthCategoryMap,
                    totalMinutes = currentMonthTotalMinutes,
                    title = "Monthly Category Distribution"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun WeeklyOverviewCard(
    report: TimeLoomAlgorithms.WeeklyReport,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardGlowBorderBrush)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL FOCUS THIS WEEK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = SpaceGrotesk,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AccentCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${report.totalTrackedMinutes / 60}h ${report.totalTrackedMinutes % 60}m",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                // Comparison badge
                val isPositive = report.previousWeekComparisonPercent >= 0
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = (if (isPositive) SuccessGreen else DangerRose).copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        (if (isPositive) SuccessGreen else DangerRose).copy(alpha = 0.35f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown,
                            contentDescription = null,
                            tint = if (isPositive) SuccessGreenGlow else DangerRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${if (isPositive) "+" else ""}${report.previousWeekComparisonPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isPositive) SuccessGreenGlow else DangerRose
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Daily Average", style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans), color = TextMuted)
                    Text(
                        "${report.averageDailyMinutes}m / day",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }
                Column {
                    Text("Peak Day", style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans), color = TextMuted)
                    Text(
                        report.mostActiveDay?.getDisplayName(TextStyle.FULL, Locale.getDefault()) ?: "N/A",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AccentCyan
                    )
                }
                Column {
                    Text("Consistency", style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans), color = TextMuted)
                    Text(
                        "${report.dailyActivities.count { it.trackedMinutes > 0 }} / 7 days",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyBarChartCard(
    report: TimeLoomAlgorithms.WeeklyReport,
    modifier: Modifier = Modifier
) {
    val maxMinutes = report.dailyActivities.maxOfOrNull { it.trackedMinutes }?.coerceAtLeast(60) ?: 60

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Daily Activity Distribution (Mon–Sun)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            // Equalizer style bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                report.dailyActivities.forEach { day ->
                    val barFraction = (day.trackedMinutes.toFloat() / maxMinutes).coerceIn(0.05f, 1f)
                    val isMostActive = day.dayOfWeek == report.mostActiveDay && day.trackedMinutes > 0

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Text(
                            text = if (day.trackedMinutes > 0) "${day.trackedMinutes}m" else "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = SpaceGrotesk,
                                fontSize = 10.sp,
                                fontWeight = if (isMostActive) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isMostActive) AccentCyan else TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSlate),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(fraction = barFraction)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isMostActive) CyanBlueGlowBrush else Brush.verticalGradient(listOf(AccentBlue, AccentBlue.copy(alpha = 0.6f))))
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = if (isMostActive) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isMostActive) AccentCyan else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDistributionCard(
    categoryMap: Map<String, Int>,
    totalMinutes: Int,
    title: String = "Category-Wise Time Allocation",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            if (categoryMap.isEmpty() || totalMinutes == 0) {
                Text(
                    text = "No category data recorded for this period.",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans),
                    color = TextMuted
                )
            } else {
                categoryMap.entries.sortedByDescending { it.value }.forEach { (cat, mins) ->
                    val fraction = (mins.toFloat() / totalMinutes).coerceIn(0f, 1f)
                    val percentage = (fraction * 100).toInt()
                    val catColor = getCategoryColor(cat)

                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(catColor))
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "${mins}m ($percentage%)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = SpaceGrotesk,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextSecondary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = catColor,
                            trackColor = DarkSlate
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskStatusSummaryCard(
    completed: Int,
    pending: Int,
    modifier: Modifier = Modifier
) {
    val total = completed + pending
    val rate = if (total > 0) ((completed.toFloat() / total) * 100).toInt() else 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$completed",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SuccessGreenGlow
                )
                Text("Completed", style = MaterialTheme.typography.labelSmall.copy(fontFamily = PlusJakartaSans), color = TextMuted)
            }
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(CardBorder))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$pending",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold
                    ),
                    color = WarningAmber
                )
                Text("Pending", style = MaterialTheme.typography.labelSmall.copy(fontFamily = PlusJakartaSans), color = TextMuted)
            }
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(CardBorder))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$rate%",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold
                    ),
                    color = AccentCyan
                )
                Text("Completion", style = MaterialTheme.typography.labelSmall.copy(fontFamily = PlusJakartaSans), color = TextMuted)
            }
        }
    }
}

@Composable
fun MonthlyOverviewCard(
    totalMinutes: Int,
    completedTasks: Int,
    totalTasks: Int,
    sessionsCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardGlowBorderBrush)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "MONTHLY TELEMETRY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = SpaceGrotesk,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = AccentCyan
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${totalMinutes / 60}h ${totalMinutes % 60}m",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Total Monthly Focus Time",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans),
                        color = TextMuted
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AccentBlue.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "$sessionsCount sessions",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AccentCyan
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tasks Solved", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Text("$completedTasks / $totalTasks", style = MaterialTheme.typography.titleMedium.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold), color = TextPrimary)
                }
                Column {
                    Text("Consistency", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Text("Consistent", style = MaterialTheme.typography.titleMedium.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold), color = SuccessGreenGlow)
                }
                Column {
                    Text("Weekly Avg", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Text("${totalMinutes / 4}m", style = MaterialTheme.typography.titleMedium.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold), color = AccentCyan)
                }
            }
        }
    }
}
