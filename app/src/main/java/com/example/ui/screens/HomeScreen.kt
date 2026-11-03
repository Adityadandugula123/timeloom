package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskStatus
import com.example.ui.components.DailyGoalProgressCard
import com.example.ui.components.StatMetricCard
import com.example.ui.components.TaskCard
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
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.TimeLoomTab
import com.example.ui.viewmodel.TimeLoomViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewModel: TimeLoomViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    val todayMinutes by viewModel.todayTrackedMinutes.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()
    val productivity by viewModel.productivityScore.collectAsState()

    val todayFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
    val completedCount = tasks.count { it.status.equals(TaskStatus.COMPLETED.name, ignoreCase = true) }
    val pendingCount = tasks.count { !it.status.equals(TaskStatus.COMPLETED.name, ignoreCase = true) }

    val recentTasks = tasks.take(4)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavy)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Profile Greeting & Time Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AccentCyan.copy(alpha = 0.18f))
                            .border(1.5.dp, AccentCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (prefs.name.firstOrNull() ?: 'A').toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                        )
                    }
                    Column {
                        Text(
                            text = "Hello, ${prefs.name.split(" ").firstOrNull() ?: "Scholar"}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = todayFormatted,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = PlusJakartaSans
                            ),
                            color = AccentCyan
                        )
                    }
                }

                // Quick Timer Shortcut Button
                Surface(
                    modifier = Modifier
                        .testTag("home_quick_timer_button")
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.setTab(TimeLoomTab.TIMER) },
                    color = AccentCyan,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Start Timer",
                            tint = DarkNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Timer",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold
                            ),
                            color = DarkNavy
                        )
                    }
                }
            }
        }

        // Daily Time Goal Circular Progress Card
        item {
            DailyGoalProgressCard(
                todayMinutes = todayMinutes,
                goalMinutes = prefs.dailyGoalMinutes
            )
        }

        // Metric Statistics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatMetricCard(
                    title = "Completed",
                    value = "$completedCount",
                    subtitle = "Tasks resolved",
                    icon = Icons.Outlined.CheckCircle,
                    iconTint = SuccessGreenGlow,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Pending",
                    value = "$pendingCount",
                    subtitle = "Action items",
                    icon = Icons.Outlined.PendingActions,
                    iconTint = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Productivity Score Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("productivity_banner_card")
                    .clickable { viewModel.openScoreModal() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardGlowBorderBrush)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentBlue.copy(alpha = 0.2f))
                                .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Speed,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Productivity Score: ",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = SpaceGrotesk,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${productivity.overallScore} / 100",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = SpaceGrotesk,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (productivity.overallScore >= 75) SuccessGreenGlow else AccentCyan
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Formula: 40% Tasks + 40% Focus + 20% Consistency",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans),
                                color = TextSecondary
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSlate,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text = "View >",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AccentCyan
                        )
                    }
                }
            }
        }

        // Quick Navigation Buttons Row (Reports + Heatmap + New Task)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.setTab(TimeLoomTab.HEATMAP) },
                    modifier = Modifier.weight(1f).testTag("home_heatmap_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Heatmap",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = SpaceGrotesk),
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = { viewModel.setTab(TimeLoomTab.REPORTS) },
                    modifier = Modifier.weight(1f).testTag("home_view_reports_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSlate),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Assessment,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reports",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = SpaceGrotesk),
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = { viewModel.openAddTaskDialog() },
                    modifier = Modifier.weight(1.1f).testTag("home_add_task_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Task",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }

        // Recent Tasks Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Academic Tasks",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "View All (${tasks.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold
                    ),
                    color = AccentCyan,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { viewModel.setTab(TimeLoomTab.TASKS) }
                        .padding(4.dp)
                )
            }
        }

        // Tasks items
        if (recentTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No tasks yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = SpaceGrotesk),
                            color = TextSecondary
                        )
                        Text(
                            text = "Create your first task to begin tracking academic productivity.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(recentTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onToggleStatus = { viewModel.toggleTaskComplete(task) },
                    onEdit = { viewModel.openEditTaskDialog(task) },
                    onDelete = { viewModel.deleteTask(task.id) },
                    onStartTimer = {
                        viewModel.startTimer(task.id, task.title, task.category)
                        viewModel.setTab(TimeLoomTab.TIMER)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
