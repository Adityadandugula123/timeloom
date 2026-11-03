/**
 * Time Loom Algorithmic Engine (JavaScript Implementation)
 * Academic B.Tech CSE Project - Time Loom
 * 
 * Implements 6 core algorithms with exact Big-O complexity analysis:
 * Algorithm 1: Duration calculation - O(1)
 * Algorithm 2: Task filtering - O(n)
 * Algorithm 3: Daily time aggregation - O(n)
 * Algorithm 4: Heatmap generation - O(n + d)
 * Algorithm 5: Weekly report generation - O(n + d)
 * Algorithm 6: Rule-based productivity score - O(n)
 */

/**
 * ALGORITHM 1: Active Duration Calculation
 * Time Complexity: O(1) | Space Complexity: O(1)
 */
function calculateActiveDuration(startTimeMillis, endTimeMillis, pausedDurationMillis = 0) {
  if (endTimeMillis <= startTimeMillis) return 0;
  const grossElapsed = endTimeMillis - startTimeMillis;
  const netElapsed = grossElapsed - pausedDurationMillis;
  return Math.max(0, Math.floor(netElapsed / 1000));
}

/**
 * ALGORITHM 2: Multi-Criteria Task Filtering
 * Time Complexity: O(n) | Space Complexity: O(k)
 */
function filterTasks(tasks, query = '', category = null, status = null, priority = null) {
  const trimmed = query.trim().toLowerCase();
  return tasks.filter(task => {
    const matchQuery = !trimmed ||
      (task.title && task.title.toLowerCase().includes(trimmed)) ||
      (task.description && task.description.toLowerCase().includes(trimmed));
    const matchCat = !category || category === 'All' || task.category.toLowerCase() === category.toLowerCase();
    const matchStat = !status || status === 'All' || task.status.toLowerCase() === status.toLowerCase();
    const matchPrio = !priority || priority === 'All' || task.priority.toLowerCase() === priority.toLowerCase();

    return matchQuery && matchCat && matchStat && matchPrio;
  });
}

/**
 * ALGORITHM 3: Daily Time Aggregation
 * Time Complexity: O(n) | Space Complexity: O(u)
 */
function aggregateDailyMinutes(sessions) {
  const dailyMap = {};
  for (const s of sessions) {
    const date = s.dateString;
    const minutes = Math.floor(s.durationSeconds / 60);
    dailyMap[date] = (dailyMap[date] || 0) + minutes;
  }
  return dailyMap;
}

/**
 * ALGORITHM 4: Contribution Heatmap Matrix Generation
 * Time Complexity: O(n + d) | Space Complexity: O(d)
 */
function getIntensityLevel(minutes) {
  if (minutes <= 0) return 0;
  if (minutes <= 30) return 1;
  if (minutes <= 60) return 2;
  if (minutes <= 120) return 3;
  return 4;
}

function generateMonthHeatmap(sessions, year, month) {
  const dailyTotals = aggregateDailyMinutes(sessions);
  const daysInMonth = new Date(year, month, 0).getDate();
  const result = [];

  for (let day = 1; day <= daysInMonth; day++) {
    const padDay = String(day).padStart(2, '0');
    const padMonth = String(month).padStart(2, '0');
    const dateStr = `${year}-${padMonth}-${padDay}`;
    const minutes = dailyTotals[dateStr] || 0;
    const level = getIntensityLevel(minutes);

    result.push({
      dateString: dateStr,
      dayOfMonth: day,
      trackedMinutes: minutes,
      intensityLevel: level
    });
  }
  return result;
}

/**
 * ALGORITHM 5: Weekly Report Aggregation
 * Time Complexity: O(n + d) | Space Complexity: O(1)
 */
function generateWeeklyReport(sessions, tasks, referenceDate = new Date()) {
  const curr = new Date(referenceDate);
  const dayOfWeek = curr.getDay(); // 0 is Sun, 1 is Mon
  const diffToMon = (dayOfWeek === 0 ? -6 : 1) - dayOfWeek;
  const monday = new Date(curr.setDate(curr.getDate() + diffToMon));
  monday.setHours(0, 0, 0, 0);

  const sunday = new Date(monday);
  sunday.setDate(sunday.getDate() + 6);
  sunday.setHours(23, 59, 59, 999);

  let totalMinutesCurrent = 0;
  const categoryMap = {};

  for (const s of sessions) {
    const sDate = new Date(s.startTime);
    if (sDate >= monday && sDate <= sunday) {
      const minutes = Math.floor(s.durationSeconds / 60);
      totalMinutesCurrent += minutes;
      const cat = s.category || 'Other';
      categoryMap[cat] = (categoryMap[cat] || 0) + minutes;
    }
  }

  const completedCount = tasks.filter(t => t.status === 'Completed').length;
  const pendingCount = tasks.filter(t => t.status !== 'Completed').length;

  return {
    weekStart: monday.toISOString().split('T')[0],
    weekEnd: sunday.toISOString().split('T')[0],
    totalTrackedMinutes: totalMinutesCurrent,
    averageDailyMinutes: Math.round(totalMinutesCurrent / 7),
    completedTasksCount: completedCount,
    pendingTasksCount: pendingCount,
    categoryBreakdown: categoryMap
  };
}

/**
 * ALGORITHM 6: Rule-Based Productivity Score
 * Time Complexity: O(n) | Space Complexity: O(1)
 */
function calculateProductivityScore(tasks, sessions, dailyGoalMinutes = 120, todayDateString = null) {
  const todayStr = todayDateString || new Date().toISOString().split('T')[0];

  // 1. Task Completion Score (40% weight)
  const totalTasks = tasks.length;
  const completedTasks = tasks.filter(t => t.status === 'Completed').length;
  const taskCompletionScore = totalTasks === 0 ? 100 : Math.round((completedTasks / totalTasks) * 100);

  // 2. Focus Time Score (40% weight)
  const todayMinutes = sessions
    .filter(s => s.dateString === todayStr)
    .reduce((sum, s) => sum + Math.floor(s.durationSeconds / 60), 0);

  const focusTimeScore = dailyGoalMinutes <= 0 ? 100 : Math.min(100, Math.round((todayMinutes / dailyGoalMinutes) * 100));

  // 3. Consistency Score (20% weight)
  const activeDates = new Set(sessions.map(s => s.dateString));
  // check last 7 days
  let activeDaysCount = 0;
  const base = new Date(todayStr);
  for (let i = 0; i < 7; i++) {
    const d = new Date(base);
    d.setDate(d.getDate() - i);
    const dStr = d.toISOString().split('T')[0];
    if (activeDates.has(dStr)) activeDaysCount++;
  }
  const consistencyScore = Math.round((activeDaysCount / 7) * 100);

  const overall = Math.round(
    (0.40 * taskCompletionScore) +
    (0.40 * focusTimeScore) +
    (0.20 * consistencyScore)
  );

  return {
    overallScore: Math.min(100, Math.max(0, overall)),
    taskCompletionScore,
    focusTimeScore,
    consistencyScore,
    explanation: overall >= 80 ? 'Exceptional academic consistency and focus execution.' :
      overall >= 60 ? 'Solid performance. Approaching daily focus goals.' :
      'Build momentum with consistent daily focus intervals.'
  };
}

module.exports = {
  calculateActiveDuration,
  filterTasks,
  aggregateDailyMinutes,
  generateMonthHeatmap,
  generateWeeklyReport,
  calculateProductivityScore,
  getIntensityLevel
};
