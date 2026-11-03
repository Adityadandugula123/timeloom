package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SessionEntity
import com.example.data.model.TaskCategory
import com.example.ui.components.CategoryChip
import com.example.ui.components.InteractiveStopwatchFace
import com.example.ui.components.getCategoryColor
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.CardGlowBorderBrush
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TimerScreen(
    viewModel: TimeLoomViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.activeTimerState.collectAsState()
    val displaySeconds by viewModel.timerDisplaySeconds.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()

    var customActivityName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.CODING.displayName) }
    var selectedTaskId by remember { mutableStateOf<Long?>(null) }
    var isTaskDropdownExpanded by remember { mutableStateOf(false) }

    // Format timer seconds to HH:MM:SS
    val hours = displaySeconds / 3600
    val minutes = (displaySeconds % 3600) / 60
    val seconds = displaySeconds % 60
    val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)

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
                        text = "Focus Stopwatch",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Algorithm 1: Timestamp-Delta Math with Pause Subtraction",
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
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Timer Dial Display Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timer_dial_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardGlowBorderBrush)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Active Task/Activity Label
                    val activeTitle = if (timerState.isRunning) {
                        timerState.taskTitle
                    } else {
                        if (selectedTaskId != null) {
                            allTasks.firstOrNull { it.id == selectedTaskId }?.title ?: "Focus Sprint"
                        } else if (customActivityName.isNotBlank()) {
                            customActivityName
                        } else {
                            "Ready to Focus"
                        }
                    }

                    val activeCat = if (timerState.isRunning) timerState.category else selectedCategory
                    val catColor = getCategoryColor(activeCat)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = catColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, catColor.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(catColor))
                                Text(
                                    text = activeCat,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = SpaceGrotesk,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = catColor
                                )
                            }
                        }
                    }

                    Text(
                        text = activeTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Analog-Digital Stopwatch Face
                    InteractiveStopwatchFace(
                        seconds = displaySeconds,
                        isRunning = timerState.isRunning,
                        isPaused = timerState.isPaused,
                        formattedTime = formattedTime
                    )

                    // Control Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!timerState.isRunning) {
                            // Start Focus Button
                            Button(
                                onClick = {
                                    val titleToUse = if (selectedTaskId != null) {
                                        allTasks.firstOrNull { it.id == selectedTaskId }?.title ?: "Focus Sprint"
                                    } else if (customActivityName.isNotBlank()) {
                                        customActivityName.trim()
                                    } else {
                                        "General Study Sprint"
                                    }
                                    viewModel.startTimer(selectedTaskId, titleToUse, selectedCategory)
                                },
                                modifier = Modifier
                                    .testTag("timer_btn_start")
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = DarkNavy,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start Focus Sprint",
                                    color = DarkNavy,
                                    fontFamily = SpaceGrotesk,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        } else {
                            // Running State: Pause / Resume + Stop
                            if (timerState.isPaused) {
                                Button(
                                    onClick = { viewModel.resumeTimer() },
                                    modifier = Modifier
                                        .testTag("timer_btn_resume")
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenGlow),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = DarkNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Resume",
                                        color = DarkNavy,
                                        fontFamily = SpaceGrotesk,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.pauseTimer() },
                                    modifier = Modifier
                                        .testTag("timer_btn_pause")
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Pause,
                                        contentDescription = null,
                                        tint = DarkNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pause",
                                        color = DarkNavy,
                                        fontFamily = SpaceGrotesk,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.stopTimer() },
                                modifier = Modifier
                                    .testTag("timer_btn_stop")
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DangerRose),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Stop,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Stop & Save",
                                    color = Color.White,
                                    fontFamily = SpaceGrotesk,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Configuration Section when not running
        if (!timerState.isRunning) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Configure Session",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        // Associate with task dropdown
                        Box {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { isTaskDropdownExpanded = true }
                                    .testTag("timer_task_selector"),
                                color = DarkSlate,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val selectedTaskTitle = allTasks.firstOrNull { it.id == selectedTaskId }?.title
                                        ?: "Select an Academic Task (Optional)"
                                    Text(
                                        text = selectedTaskTitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = PlusJakartaSans),
                                        color = if (selectedTaskId != null) AccentCyan else TextSecondary,
                                        maxLines = 1
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = null,
                                        tint = TextMuted
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isTaskDropdownExpanded,
                                onDismissRequest = { isTaskDropdownExpanded = false },
                                modifier = Modifier.background(DarkSlate)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("None (Freeform focus)", color = TextPrimary) },
                                    onClick = {
                                        selectedTaskId = null
                                        isTaskDropdownExpanded = false
                                    }
                                )
                                allTasks.filter { it.status != "COMPLETED" }.forEach { task ->
                                    DropdownMenuItem(
                                        text = { Text(task.title, color = TextPrimary) },
                                        onClick = {
                                            selectedTaskId = task.id
                                            selectedCategory = task.category
                                            isTaskDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Or type custom activity name
                        if (selectedTaskId == null) {
                            OutlinedTextField(
                                value = customActivityName,
                                onValueChange = { customActivityName = it },
                                label = { Text("Or Enter Activity Title", color = TextSecondary) },
                                placeholder = { Text("e.g. Compiler Design Lab Preparation", color = TextMuted) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("timer_custom_activity_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkSlate,
                                    unfocusedContainerColor = DarkSlate
                                )
                            )
                        }

                        // Category chips
                        Text(
                            text = "Category Tag",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = SpaceGrotesk),
                            color = TextSecondary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Study", "Coding", "Project", "Research").forEach { cat ->
                                CategoryChip(
                                    category = cat,
                                    isSelected = selectedCategory.equals(cat, ignoreCase = true),
                                    onClick = { selectedCategory = cat }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Screen 6: Activity History Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Recent Focus History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSlate,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = "${allSessions.size} records",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = SpaceGrotesk),
                        color = TextSecondary
                    )
                }
            }
        }

        if (allSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = "No recorded sessions yet. Completed sessions will be listed here with timestamps and duration.",
                        modifier = Modifier.padding(18.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(allSessions.take(8), key = { it.id }) { session ->
                SessionHistoryItem(session = session, onDelete = { viewModel.deleteSession(session.id) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun SessionHistoryItem(
    session: SessionEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMinutes = session.durationSeconds / 60
    val startInstant = Instant.ofEpochMilli(session.startTime).atZone(ZoneId.systemDefault())
    val endInstant = Instant.ofEpochMilli(session.endTime).atZone(ZoneId.systemDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val catColor = getCategoryColor(session.category)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = session.activityName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = catColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, catColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = session.category,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = PlusJakartaSans),
                            color = catColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${session.dateString} • ${timeFormatter.format(startInstant)} – ${timeFormatter.format(endInstant)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans),
                    color = TextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${durationMinutes}m",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Session",
                        tint = DangerRose.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
