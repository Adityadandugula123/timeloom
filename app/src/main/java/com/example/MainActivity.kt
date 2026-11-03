package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddEditTaskDialog
import com.example.ui.screens.AlgorithmVivaScreen
import com.example.ui.screens.HeatmapScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProductivityScoreScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DarkSlate
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TimeLoomTheme
import com.example.ui.viewmodel.TimeLoomTab
import com.example.ui.viewmodel.TimeLoomViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeLoomTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkNavy
                ) {
                    TimeLoomApp()
                }
            }
        }
    }
}

@Composable
fun TimeLoomApp(
    viewModel: TimeLoomViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showSettings by viewModel.showSettings.collectAsState()
    val showViva by viewModel.showVivaScreen.collectAsState()
    val showScoreModal by viewModel.showScoreModal.collectAsState()
    val isAddEditDialogOpen by viewModel.isAddEditDialogOpen.collectAsState()
    val editingTask by viewModel.editingTask.collectAsState()

    var hasCompletedOnboarding by remember { mutableStateOf(true) }

    when {
        !hasCompletedOnboarding -> {
            OnboardingScreen(
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
                onGetStarted = { hasCompletedOnboarding = true }
            )
        }
        showViva -> {
            BackHandler { viewModel.closeVivaScreen() }
            AlgorithmVivaScreen(
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
                onBack = { viewModel.closeVivaScreen() }
            )
        }
        showSettings -> {
            BackHandler { viewModel.closeSettings() }
            ProfileSettingsScreen(
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
                viewModel = viewModel,
                onBack = { viewModel.closeSettings() }
            )
        }
        showScoreModal -> {
            BackHandler { viewModel.closeScoreModal() }
            ProductivityScoreScreen(
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
                viewModel = viewModel,
                onBack = { viewModel.closeScoreModal() }
            )
        }
        else -> {
            // Task Creation/Editing Dialog
            if (isAddEditDialogOpen) {
                AddEditTaskDialog(
                    taskToEdit = editingTask,
                    onDismiss = { viewModel.closeAddEditDialog() },
                    onSave = { id, title, desc, cat, prio, due, dur ->
                        viewModel.saveTask(id, title, desc, cat, prio, due, dur)
                    }
                )
            }

            Scaffold(
                modifier = Modifier.fillMaxSize().background(DarkNavy),
                topBar = {
                    TimeLoomTopBar(
                        onOpenSettings = { viewModel.openSettings() }
                    )
                },
                bottomBar = {
                    TimeLoomBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) }
                    )
                },
                containerColor = DarkNavy
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            TimeLoomTab.HOME -> HomeScreen(viewModel = viewModel)
                            TimeLoomTab.TASKS -> TasksScreen(viewModel = viewModel)
                            TimeLoomTab.TIMER -> TimerScreen(viewModel = viewModel)
                            TimeLoomTab.REPORTS -> ReportsScreen(viewModel = viewModel)
                            TimeLoomTab.HEATMAP -> HeatmapScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeLoomTopBar(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkNavy
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Name & Live Glowing Nexus Indicator
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentCyan.copy(alpha = 0.15f))
                        .border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timeline,
                        contentDescription = "Logo",
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "TIME LOOM",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Track. Understand. Improve.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = PlusJakartaSans,
                            fontSize = 9.sp
                        ),
                        color = AccentCyan,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Settings button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSlate)
                    .testTag("top_btn_settings")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun TimeLoomBottomBar(
    currentTab: TimeLoomTab,
    onTabSelected: (TimeLoomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = DarkSlate,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeLoomNavItem(
                tab = TimeLoomTab.HOME,
                selected = currentTab == TimeLoomTab.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                onClick = { onTabSelected(TimeLoomTab.HOME) }
            )
            TimeLoomNavItem(
                tab = TimeLoomTab.TASKS,
                selected = currentTab == TimeLoomTab.TASKS,
                selectedIcon = Icons.Filled.Checklist,
                unselectedIcon = Icons.Outlined.Checklist,
                onClick = { onTabSelected(TimeLoomTab.TASKS) }
            )
            TimeLoomNavItem(
                tab = TimeLoomTab.TIMER,
                selected = currentTab == TimeLoomTab.TIMER,
                selectedIcon = Icons.Filled.Timer,
                unselectedIcon = Icons.Outlined.Timer,
                onClick = { onTabSelected(TimeLoomTab.TIMER) }
            )
            TimeLoomNavItem(
                tab = TimeLoomTab.REPORTS,
                selected = currentTab == TimeLoomTab.REPORTS,
                selectedIcon = Icons.Filled.Assessment,
                unselectedIcon = Icons.Outlined.Assessment,
                onClick = { onTabSelected(TimeLoomTab.REPORTS) }
            )
            TimeLoomNavItem(
                tab = TimeLoomTab.HEATMAP,
                selected = currentTab == TimeLoomTab.HEATMAP,
                selectedIcon = Icons.Filled.CalendarMonth,
                unselectedIcon = Icons.Outlined.CalendarMonth,
                onClick = { onTabSelected(TimeLoomTab.HEATMAP) }
            )
        }
    }
}

@Composable
fun TimeLoomNavItem(
    tab: TimeLoomTab,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (selected) AccentCyan.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(200),
        label = "nav_bg"
    )
    val tint by animateColorAsState(
        targetValue = if (selected) AccentCyan else TextMuted,
        animationSpec = tween(200),
        label = "nav_tint"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(animatedBg)
            .then(
                if (selected) Modifier.border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("nav_tab_${tab.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = tab.title,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = tab.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = SpaceGrotesk,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = tint
        )
    }
}
