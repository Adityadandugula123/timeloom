package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorithms.TimeLoomAlgorithms
import com.example.ui.components.intensityColor
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentCyanLight
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.CardGlowBorderBrush
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DarkSlate
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TimeLoomViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HeatmapScreen(
    viewModel: TimeLoomViewModel,
    modifier: Modifier = Modifier
) {
    val heatmapDays by viewModel.monthHeatmap.collectAsState()
    val currentMonthDate by viewModel.currentHeatmapDate.collectAsState()
    val selectedDate by viewModel.selectedHeatmapDate.collectAsState()
    val selectedDateSessions by viewModel.selectedDateSessions.collectAsState()

    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val totalMonthMinutes = heatmapDays.sumOf { it.trackedMinutes }
    val activeDaysCount = heatmapDays.count { it.trackedMinutes > 0 }

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
                        text = "Temporal Heatmap",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Algorithm 4: Calendar Matrix (Levels 0–4)",
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
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Monthly Stats Overview Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardGlowBorderBrush)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${totalMonthMinutes / 60}h ${totalMonthMinutes % 60}m",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Month Total",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = SpaceGrotesk),
                            color = TextSecondary
                        )
                    }
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(CardBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$activeDaysCount / ${heatmapDays.size}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                        )
                        Text(
                            text = "Active Days",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = SpaceGrotesk),
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Month Calendar Heatmap Grid Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("heatmap_calendar_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Month Navigation Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.previousMonth() },
                            modifier = Modifier.testTag("heatmap_prev_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Month",
                                tint = AccentCyan
                            )
                        }

                        Text(
                            text = monthFormatter.format(currentMonthDate),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        IconButton(
                            onClick = { viewModel.nextMonth() },
                            modifier = Modifier.testTag("heatmap_next_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Month",
                                tint = AccentCyan
                            )
                        }
                    }

                    // Days of week header (Mon to Sun)
                    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEach { dayName ->
                            Text(
                                text = dayName,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = SpaceGrotesk,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextMuted
                            )
                        }
                    }

                    // Calendar Grid Layout
                    val firstDayOfMonth = LocalDate.of(currentMonthDate.year, currentMonthDate.month, 1)
                    val leadOffset = firstDayOfMonth.dayOfWeek.value - 1 // 0 for Mon, 6 for Sun

                    val totalCells = leadOffset + heatmapDays.size
                    val rowCount = (totalCells + 6) / 7

                    for (r in 0 until rowCount) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            for (c in 0..6) {
                                val cellIndex = r * 7 + c
                                val dayIndex = cellIndex - leadOffset

                                if (dayIndex in heatmapDays.indices) {
                                    val dayData = heatmapDays[dayIndex]
                                    val isSelected = selectedDate == dayData.date
                                    val isToday = dayData.date == LocalDate.now()

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(intensityColor(dayData.intensityLevel))
                                            .then(
                                                if (isSelected) {
                                                    Modifier.border(2.dp, Color.White, RoundedCornerShape(8.dp))
                                                } else if (isToday) {
                                                    Modifier.border(1.5.dp, AccentCyan, RoundedCornerShape(8.dp))
                                                } else {
                                                    Modifier
                                                }
                                            )
                                            .clickable { viewModel.selectHeatmapDate(dayData.date) }
                                            .testTag("heatmap_cell_${dayData.dateString}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${dayData.dayOfMonth}",
                                            fontFamily = SpaceGrotesk,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                            color = if (dayData.intensityLevel >= 2) Color.White else TextSecondary
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Heatmap Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Less",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = PlusJakartaSans),
                            color = TextMuted,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        val levels = listOf(0, 1, 2, 3, 4)
                        levels.forEach { lvl ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(intensityColor(lvl))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        Text(
                            text = "More",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = PlusJakartaSans),
                            color = TextMuted,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Text(
                        text = "Levels: 0 (0m) • 1 (1–30m) • 2 (31–60m) • 3 (61–120m) • 4 (>120m)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = SpaceGrotesk,
                            fontSize = 10.sp
                        ),
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Selected Day Activity Details
        item {
            val dateLabel = selectedDate?.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")) ?: "Selected Day"
            val dayTotalMinutes = selectedDateSessions.sumOf { it.durationSeconds } / 60
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentCyan.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "$dayTotalMinutes mins total",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AccentCyan
                    )
                }
            }
        }

        if (selectedDateSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = "No focus sessions recorded on this date.",
                        modifier = Modifier.padding(18.dp),
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans),
                        color = TextMuted
                    )
                }
            }
        } else {
            items(selectedDateSessions, key = { it.id }) { session ->
                SessionHistoryItem(
                    session = session,
                    onDelete = { viewModel.deleteSession(session.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
