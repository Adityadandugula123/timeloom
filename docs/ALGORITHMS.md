# TIME LOOM — Algorithm Design, Specification & Analysis
**Academic B.Tech Computer Science Engineering Deliverable**  
**Project:** TIME LOOM — Track. Understand. Improve.  
**Author:** Aditya Dandugula (B.Tech CSE)  

---

## Executive Summary of Algorithmic Core
TIME LOOM implements a deterministic, transparent algorithmic framework. It avoids black-box heuristics or simulated percentiles in favor of mathematically verifiable formulas with known computational bounds.

| ID | Algorithm Name | Time Complexity | Space Complexity | Primary Responsibility |
|---|---|---|---|---|
| **Algo 1** | Active Duration Calculation | $O(1)$ | $O(1)$ | High-precision time delta across pause cycles |
| **Algo 2** | Multi-Criteria Task Filtering | $O(n)$ | $O(k), k \le n$ | Linear predicate evaluation across task metadata |
| **Algo 3** | Daily Time Aggregation | $O(n)$ | $O(u), u \le 31$ | Hash-based temporal binning of session durations |
| **Algo 4** | Heatmap Matrix Generation | $O(n + d)$ | $O(d)$ | Calendar grid contribution mapping (Levels 0–4) |
| **Algo 5** | Weekly Report Aggregation | $O(n + d)$ | $O(1)$ aux | Monday–Sunday metrics, category distributions, deltas |
| **Algo 6** | Rule-Based Productivity Score | $O(n)$ | $O(1)$ | Composite index: $0.40 \cdot \text{Tasks} + 0.40 \cdot \text{Focus} + 0.20 \cdot \text{Consistency}$ |

---

## 1. Algorithm 1: Active Duration Calculation
### Problem Statement
In mobile environments, apps are routinely backgrounded, suspended, or put into standby. A simple periodic interval counter (`setInterval` or timer ticker) fails when the OS pauses the process. This algorithm computes the true active duration by taking the difference between absolute start and end epoch timestamps, subtracting the cumulative paused duration.

### Formal Specification
- **Input:** 
  - $t_{\text{start}} \in \mathbb{Z}^+$ (Epoch timestamp in milliseconds when timer started)
  - $t_{\text{end}} \in \mathbb{Z}^+$ (Epoch timestamp in milliseconds when timer stopped)
  - $\Delta t_{\text{paused}} \in \mathbb{Z}^*$ (Accumulated paused duration in milliseconds)
- **Output:** 
  - $D_{\text{seconds}} \in \mathbb{Z}_{\ge 0}$ (Net active duration in seconds)

### Step-by-Step Logic
1. Validate invariant: if $t_{\text{end}} \le t_{\text{start}}$, return 0.
2. Calculate gross elapsed duration: $\Delta t_{\text{gross}} = t_{\text{end}} - t_{\text{start}}$.
3. Subtract accumulated pause time: $\Delta t_{\text{net}} = \Delta t_{\text{gross}} - \Delta t_{\text{paused}}$.
4. Convert net milliseconds to whole seconds: $D_{\text{seconds}} = \max(0, \lfloor \Delta t_{\text{net}} / 1000 \rfloor)$.
5. Return $D_{\text{seconds}}$.

### Pseudocode
```text
FUNCTION calculateActiveDuration(startTimeMillis, endTimeMillis, pausedDurationMillis):
    IF endTimeMillis <= startTimeMillis THEN
        RETURN 0
    grossElapsed = endTimeMillis - startTimeMillis
    netElapsed = grossElapsed - pausedDurationMillis
    seconds = MAX(0, FLOOR(netElapsed / 1000))
    RETURN seconds
END FUNCTION
```

### Control Flow Diagram
```text
[Start]
   |
   v
[Check: endTime > startTime?] --(No)--> [Return 0]
   | (Yes)
   v
[gross = endTime - startTime]
   |
   v
[net = gross - pausedDuration]
   |
   v
[sec = MAX(0, FLOOR(net / 1000))]
   |
   v
[Return sec] -> [End]
```

### Complexity Analysis
- **Time Complexity:** $O(1)$ — Consists solely of elementary arithmetic comparisons, subtractions, and division.
- **Space Complexity:** $O(1)$ — No auxiliary storage allocated.

### Dry Run Example
- **Input:** $t_{\text{start}} = 1,730,592,000,000$, $t_{\text{end}} = 1,730,594,730,000$, $\Delta t_{\text{paused}} = 30,000$.
- **Gross:** $1,730,594,730,000 - 1,730,592,000,000 = 2,730,000$ ms.
- **Net:** $2,730,000 - 30,000 = 2,700,000$ ms.
- **Seconds:** $\lfloor 2,700,000 / 1000 \rfloor = 2,700$ seconds (45 minutes). Output: **2700**.

---

## 2. Algorithm 2: Multi-Criteria Task Filtering
### Problem Statement
Users need to quickly isolate tasks across orthogonal dimensions: text search (case-insensitive substring in title or description), category classification, completion status, and priority level.

### Formal Specification
- **Input:** 
  - $T = \{t_1, t_2, \dots, t_n\}$ (Array of Task records)
  - $Q \in \text{String}$ (Search query)
  - $C \in \text{Category} \cup \{\text{null}, \text{"All"}\}$
  - $S \in \text{Status} \cup \{\text{null}, \text{"All"}\}$
  - $P \in \text{Priority} \cup \{\text{null}, \text{"All"}\}$
- **Output:** 
  - $T' \subseteq T$ satisfying the conjunction of all filter predicates.

### Pseudocode
```text
FUNCTION filterTasks(tasks, query, category, status, priority):
    result = []
    q = LOWERCASE(TRIM(query))
    FOR EACH task IN tasks:
        matchQuery = (q is EMPTY) OR (q IN LOWERCASE(task.title + " " + task.description))
        matchCat = (category is NULL or "All") OR (LOWERCASE(task.category) == LOWERCASE(category))
        matchStat = (status is NULL or "All") OR (LOWERCASE(task.status) == LOWERCASE(status))
        matchPrio = (priority is NULL or "All") OR (LOWERCASE(task.priority) == LOWERCASE(priority))
        
        IF matchQuery AND matchCat AND matchStat AND matchPrio THEN
            result.ADD(task)
    RETURN result
END FUNCTION
```

### Complexity Analysis
- **Time Complexity:** $O(n \cdot m)$ where $n$ is task count and $m$ is average string length. Because string lengths are bounded ($m \le 100$), this simplifies to $O(n)$.
- **Space Complexity:** $O(k)$ where $k \le n$ is the count of matching tasks.

---

## 3. Algorithm 3: Daily Time Aggregation
### Problem Statement
Given an unaggregated collection of recorded time sessions, group records by calendar date and compute the total active minutes logged per day.

### Formal Specification
- **Input:** $S = \{s_1, s_2, \dots, s_n\}$ where each $s_i$ contains `dateString` and `durationSeconds`.
- **Output:** Hash map $M: \text{DateString} \to \text{TotalMinutes}$.

### Pseudocode
```text
FUNCTION aggregateDailyMinutes(sessions):
    dailyMap = NEW HASH_MAP()
    FOR EACH session IN sessions:
        dateKey = session.dateString
        minutes = FLOOR(session.durationSeconds / 60)
        dailyMap[dateKey] = dailyMap.GET_OR_DEFAULT(dateKey, 0) + minutes
    RETURN dailyMap
END FUNCTION
```

### Complexity Analysis
- **Time Complexity:** $O(n)$ — Linear pass over $n$ sessions with $O(1)$ amortized hash map lookups and updates.
- **Space Complexity:** $O(u)$ where $u \le n$ is the number of distinct dates.

---

## 4. Algorithm 4: Heatmap Matrix Generation
### Problem Statement
Produce an activity heatmap for a given calendar month with discrete intensity classifications:
- **Level 0:** 0 minutes (no activity)
- **Level 1:** 1 – 30 minutes
- **Level 2:** 31 – 60 minutes
- **Level 3:** 61 – 120 minutes
- **Level 4:** > 120 minutes

### Formal Specification
- **Input:** $S$ (Sessions array), $Y$ (Year), $M$ (Month $1..12$).
- **Output:** Array of $d$ `HeatmapDay` entries, where $d \in \{28, 29, 30, 31\}$.

### Pseudocode
```text
FUNCTION generateMonthHeatmap(sessions, year, month):
    dailyTotals = aggregateDailyMinutes(sessions)     // O(n)
    d = DAYS_IN_MONTH(year, month)
    heatmapGrid = ARRAY[d]
    
    FOR day = 1 TO d:                                 // O(d)
        dateStr = FORMAT(year, month, day)
        mins = dailyTotals.GET_OR_DEFAULT(dateStr, 0)
        
        IF mins <= 0: level = 0
        ELSE IF mins <= 30: level = 1
        ELSE IF mins <= 60: level = 2
        ELSE IF mins <= 120: level = 3
        ELSE: level = 4
        
        heatmapGrid.ADD(HeatmapDay(day, mins, level))
    RETURN heatmapGrid
END FUNCTION
```

### Complexity Analysis
- **Time Complexity:** $O(n + d)$ where $n$ is sessions and $d \le 31$. Because $d$ is a constant upper bound, the asymptotic bound is $O(n)$.
- **Space Complexity:** $O(d)$ for the output array.

---

## 5. Algorithm 5: Weekly Report Aggregation
### Problem Statement
Aggregate activity metrics over a 7-day Monday–Sunday window, extracting daily bar values, category distributions, completion ratios, and week-over-week deltas.

### Formal Specification
- **Input:** Sessions $S$, Tasks $T$, reference date $R$.
- **Output:** `WeeklyReport` structure with total minutes, daily list, completion metrics, and % change.

### Complexity Analysis
- **Time Complexity:** $O(n + d)$ where $d = 7$.
- **Space Complexity:** $O(1)$ auxiliary memory.

---

## 6. Algorithm 6: Rule-Based Productivity Score
### Problem Statement
Compute a transparent, reproducible productivity score on a 0–100 scale using the specified weighted formula:
$$\text{Score} = (0.40 \times S_{\text{task}}) + (0.40 \times S_{\text{focus}}) + (0.20 \times S_{\text{consistency}})$$

### Definitions
1. **Task Completion Score ($S_{\text{task}}$):**
   $$S_{\text{task}} = \begin{cases} 100 & \text{if } |T| = 0 \\ \min\left(100, \left\lfloor \frac{|T_{\text{completed}}|}{|T|} \times 100 \right\rfloor\right) & \text{if } |T| > 0 \end{cases}$$
2. **Focus Time Score ($S_{\text{focus}}$):**
   $$S_{\text{focus}} = \min\left(100, \left\lfloor \frac{M_{\text{today}}}{G_{\text{daily}}} \times 100 \right\rfloor\right)$$
3. **Consistency Score ($S_{\text{consistency}}$):**
   $$S_{\text{consistency}} = \left\lfloor \frac{D_{\text{active}, 7}}{7} \times 100 \right\rfloor$$
   where $D_{\text{active}, 7}$ is the count of distinct calendar days in the last 7 days with at least one completed session.

### Pseudocode
```text
FUNCTION calculateProductivityScore(tasks, sessions, dailyGoal, today):
    // 1. Task Completion Score
    totalTasks = tasks.size
    completed = COUNT(tasks WHERE status == "Completed")
    taskScore = (totalTasks == 0) ? 100 : ROUND(completed / totalTasks * 100)

    // 2. Focus Time Score
    todayMinutes = SUM(sessions WHERE date == today)
    focusScore = (dailyGoal <= 0) ? 100 : MIN(100, ROUND(todayMinutes / dailyGoal * 100))

    // 3. Consistency Score
    activeDays = COUNT_DISTINCT(dates WITH >= 1 session in last 7 days)
    consistencyScore = ROUND(activeDays / 7 * 100)

    // Weighted Overall
    overall = (0.40 * taskScore) + (0.40 * focusScore) + (0.20 * consistencyScore)
    RETURN ROUND(overall)
END FUNCTION
```

### Complexity Analysis
- **Time Complexity:** $O(n + m)$ where $n$ is tasks and $m$ is sessions.
- **Space Complexity:** $O(1)$ auxiliary.
