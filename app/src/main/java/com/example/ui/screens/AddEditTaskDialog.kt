package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.algorithms.TimeLoomAlgorithms
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.ui.components.CategoryChip
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.LocalDate

@Composable
fun AddEditTaskDialog(
    taskToEdit: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (id: Long, title: String, description: String, category: String, priority: String, dueDate: String, expectedDuration: Int) -> Unit
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var category by remember { mutableStateOf(taskToEdit?.category ?: TaskCategory.STUDY.name) }
    var priority by remember { mutableStateOf(taskToEdit?.priority ?: TaskPriority.MEDIUM.name) }
    var dueDate by remember {
        mutableStateOf(taskToEdit?.dueDate?.ifBlank { LocalDate.now().format(TimeLoomAlgorithms.DATE_FORMATTER) }
            ?: LocalDate.now().format(TimeLoomAlgorithms.DATE_FORMATTER))
    }
    var expectedDuration by remember { mutableIntStateOf(taskToEdit?.expectedDuration ?: 45) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = TaskCategory.entries.map { it.name }
    val priorities = TaskPriority.entries.map { it.name }
    val durationOptions = listOf(15, 25, 30, 45, 60, 90, 120)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("add_edit_task_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardCharcoal),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardGlowBorderBrush)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (taskToEdit == null) "Create New Task" else "Edit Academic Task",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlusJakartaSans),
                        color = DangerRose
                    )
                }

                // Task Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) errorMessage = null
                    },
                    label = { Text("Task Title *", color = TextSecondary, fontFamily = PlusJakartaSans) },
                    placeholder = { Text("e.g. Implement Graph BFS Algorithm", color = TextMuted, fontFamily = PlusJakartaSans) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_title"),
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

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Deliverables", color = TextSecondary, fontFamily = PlusJakartaSans) },
                    placeholder = { Text("Key requirements, lab rubric, notes...", color = TextMuted, fontFamily = PlusJakartaSans) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_description"),
                    maxLines = 3,
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

                // Category Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = SpaceGrotesk),
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            CategoryChip(
                                category = cat,
                                isSelected = category.equals(cat, ignoreCase = true),
                                onClick = { category = cat }
                            )
                        }
                    }
                }

                // Priority Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Priority Level",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = SpaceGrotesk),
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        priorities.forEach { prio ->
                            CategoryChip(
                                category = prio,
                                isSelected = priority.equals(prio, ignoreCase = true),
                                onClick = { priority = prio }
                            )
                        }
                    }
                }

                // Due Date
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)", color = TextSecondary, fontFamily = PlusJakartaSans) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_due_date"),
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

                // Expected Duration selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Target Duration: $expectedDuration minutes",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = SpaceGrotesk),
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        durationOptions.forEach { dur ->
                            CategoryChip(
                                category = "${dur}m",
                                isSelected = expectedDuration == dur,
                                onClick = { expectedDuration = dur }
                            )
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancel", fontFamily = SpaceGrotesk)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter a task title"
                            } else {
                                onSave(
                                    taskToEdit?.id ?: 0L,
                                    title.trim(),
                                    description.trim(),
                                    category,
                                    priority,
                                    dueDate.trim(),
                                    expectedDuration
                                )
                            }
                        },
                        modifier = Modifier.testTag("btn_save_task"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                    ) {
                        Text(
                            text = if (taskToEdit == null) "Create Task" else "Save Changes",
                            color = DarkNavy,
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
