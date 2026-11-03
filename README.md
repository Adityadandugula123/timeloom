# TIME LOOM — Track. Understand. Improve.
### Academic B.Tech Computer Science Engineering Project
**Author:** Aditya Dandugula  
**Domain:** Personal Time-Tracking, Behavioral Telemetry & Productivity Analytics  
**Architecture:** Modern Android (Kotlin + Jetpack Compose + Room SQLite) & Node.js/Express + MongoDB REST API  

---

## 1. Project Overview & Identity
**TIME LOOM** is a personal time-tracking and productivity analytics application engineered for academic demonstration. The system bridges task management with high-precision temporal tracking, contribution heatmaps, multi-window report generation, and a transparent, rule-based productivity scoring algorithm.

### Core Visual Direction: Option 10 – Data Intelligence
- **Primary Background:** Dark Navy (`#101827`)
- **Secondary Background:** Dark Slate (`#182235`)
- **Card Background:** Slate/Charcoal (`#1E293B`)
- **Primary Accent:** Google Blue (`#4285F4`)
- **Secondary Accent:** Neon Cyan (`#22C7D8`)
- **Success:** Emerald Green (`#10B981`)
- **Warning:** Amber Orange (`#F59E0B`)

---

## 2. Academic Deliverables & Architectural Separation

```text
TIME LOOM CAPSTONE SYSTEM
│
├── Android Client (Native Jetpack Compose)
│   ├── UI: Material 3, Dark Navy Data Intelligence Theme
│   ├── Architecture: Clean Architecture + MVVM + StateFlow
│   ├── Local Persistence: Room Database (SQLite) + SharedPreferences
│   └── Algorithms: TimeLoomAlgorithms.kt (Exact O(1), O(n), O(n+d) engines)
│
├── Backend REST API (/backend)
│   ├── Runtime: Node.js + Express.js
│   ├── Database: MongoDB + Mongoose Schemas (Users, Tasks, Sessions)
│   ├── Routes: Auth, Tasks, Sessions, Analytics Reports
│   └── Algorithms: /backend/algorithms/timeLoomAlgorithms.js
│
└── Academic Documentation (/docs)
    ├── ALGORITHMS.md (Pseudocode, flowcharts, complexity, dry runs)
    ├── API_DOCUMENTATION.md (Full HTTP endpoint contract)
    └── DATABASE_SCHEMA.md (ER diagrams, collection schemas)
```

---

## 3. Screen Checklist (11 Complete Modules)
1. **Screen 1: Splash & Onboarding** — Brand identity, tagline, feature highlights, and Get Started action.
2. **Screen 2: Home Dashboard** — Greeting, today's date, circular focus goal ring, completed/pending counters, productivity score badge, active task list, Quick Start Timer.
3. **Screen 3: Task Management** — Filterable task list (All, Pending, In Progress, Completed), search bar, category chips, priority pills, duration progress bars, FAB.
4. **Screen 4: Add & Edit Task** — Validated modal sheet with title, notes, category selector, priority level, due date, and expected duration options.
5. **Screen 5: Focus Timer** — Working digital stopwatch with actual timestamp math, task attachment, pause/resume, and persistence across app restarts.
6. **Screen 6: Activity History** — Comprehensive log of tracked intervals with start/stop timestamps, duration in minutes, and category tagging.
7. **Screen 7: Calendar & Activity Heatmap** — Month calendar matrix with 5 intensity levels (0m, 1–30m, 31–60m, 61–120m, >120m), month navigation (`<` and `>`), and date drill-down.
8. **Screen 8: Weekly Reports** — Total tracked time, Monday–Sunday bar chart with active-day highlight, completed/pending stats, and category distribution.
9. **Screen 9: Monthly Reports** — Total monthly minutes, session counters, consistency ratings, and month-over-month comparisons.
10. **Screen 10: Productivity Score** — Transparent weighted calculation: $(0.40 \times \text{Task Completion}) + (0.40 \times \text{Focus Goal}) + (0.20 \times \text{Consistency})$.
11. **Screen 11: Settings & Profile** — User name, student ID, daily goal slider (30–300m), haptic feedback toggle, demo data reset.
12. **Bonus: Faculty Viva & Algorithm Showcase Screen** — Integrated mobile screen displaying formal pseudocode, flowcharts, time/space complexity, and live test dry-run execution.

---

## 4. Algorithmic Core Summary

| Algorithm | Function | Time Complexity | Space Complexity |
|---|---|---|---|
| **Algorithm 1** | Active Duration Calculation | $O(1)$ | $O(1)$ |
| **Algorithm 2** | Multi-Criteria Task Filtering | $O(n)$ | $O(k)$ |
| **Algorithm 3** | Daily Time Aggregation | $O(n)$ | $O(u)$ |
| **Algorithm 4** | Heatmap Matrix Generation | $O(n + d)$ | $O(d)$ |
| **Algorithm 5** | Weekly Report Generation | $O(n + d)$ | $O(1)$ |
| **Algorithm 6** | Rule-Based Productivity Score | $O(n)$ | $O(1)$ |

---

## 5. Execution & Verification Guide

### A. Android Mobile App (Streaming Emulator)
The native Android client builds automatically through Gradle and executes directly on the cloud emulator:
```bash
gradle :app:testDebugUnitTest    # Executes algorithmic unit tests
```

### B. Node.js Express Backend
```bash
cd backend
npm install
npm start
# Server listens at http://localhost:5000
```

---

## 6. Viva Demonstration Guide for Faculty
1. **Data Persistence Across Restarts:** Start a timer in the **Timer** tab. Restart the app or toggle tasks; note that the timer recovers exact elapsed seconds via timestamp delta math, never losing track.
2. **Deterministic Productivity Score:** Open the **Productivity** score banner from the dashboard. Show the mathematical formula and toggle tasks to watch the score recalculate transparently without fake percentiles.
3. **Heatmap Threshold Verification:** Navigate to the **Heatmap** tab. Explain the 5 levels (0 to 4) mapped to 0m, 1–30m, 31–60m, 61–120m, and >120m. Tap any date to view the underlying session records.
4. **Algorithm Showcase Screen:** Tap the **Viva Hub** button in the top bar to display the formal pseudocode, flowchart diagram, and run the live dry-run test verification right in front of the examiners.
