# TIME LOOM — Database Schema & Architecture Design
**Academic B.Tech Computer Science Engineering Deliverable**  
**Project:** TIME LOOM — Track. Understand. Improve.  

---

## 1. Architectural Overview
Time Loom provides dual-target database persistence:
1. **Device-Local Persistence (Android Room SQLite):** High-speed, offline-first local SQL database used by the streaming Android applet for instantaneous UI rendering, transactional integrity, and zero-latency queries.
2. **Backend Document Store (MongoDB + Mongoose):** Relational-referenced document collections used by the Node.js REST API for centralized cloud multi-user synchronization and reporting.

---

## 2. MongoDB Collection Schemas

### Collection: `users`
Represents student accounts and baseline productivity goals.
```javascript
{
  _id: ObjectId,              // Unique primary key
  name: String,               // Student name (required)
  email: String,              // Indexed unique email (required)
  passwordHash: String,       // Salted hash (never plaintext)
  dailyGoalMinutes: Number,   // Daily focus target in minutes (default: 120)
  createdAt: Date             // Account creation timestamp
}
```

### Collection: `tasks`
Represents academic assignments, project milestones, and study tasks.
```javascript
{
  _id: ObjectId,              // Unique primary key
  userId: ObjectId,           // Foreign key referencing users._id
  title: String,              // Task title (required)
  description: String,        // Optional task notes
  category: String,           // Enum: ['Study', 'Coding', 'Project', 'Research', 'Reading', 'Fitness', 'Other']
  priority: String,           // Enum: ['Low', 'Medium', 'High', 'Urgent']
  dueDate: String,            // ISO date string format: YYYY-MM-DD
  expectedDuration: Number,   // Planned minutes (e.g., 45)
  actualTrackedDuration: Number, // Accumulated minutes from completed sessions
  status: String,             // Enum: ['Pending', 'In Progress', 'Completed']
  createdAt: Date,
  updatedAt: Date
}
```

### Collection: `sessions`
Represents discrete time-tracking focus intervals.
```javascript
{
  _id: ObjectId,              // Unique primary key
  userId: ObjectId,           // Foreign key referencing users._id
  taskId: ObjectId,           // Optional foreign key referencing tasks._id
  activityName: String,       // Human-readable title
  category: String,           // Classification
  startTime: Number,          // Epoch timestamp in milliseconds
  endTime: Number,            // Epoch timestamp in milliseconds
  durationSeconds: Number,    // Net active duration (Algorithm 1)
  pausedDurationMillis: Number, // Accumulated pause offset
  dateString: String,         // YYYY-MM-DD (Indexed for fast daily aggregation)
  status: String,             // Enum: ['Active', 'Paused', 'Completed', 'Discarded']
  createdAt: Date
}
```

---

## 3. Entity-Relationship (ER) Diagram
```text
 +-------------------------+
 |          User           |
 +-------------------------+
 | _id (PK)                |<-------+
 | name                    |        |
 | email (UNIQUE)          |        |
 | passwordHash            |        |
 | dailyGoalMinutes        |        |
 +-------------------------+        |
              | 1                   | 1
              |                     |
              | N                   | N
 +-------------------------+        |
 |          Task           |        |
 +-------------------------+        |
 | _id (PK)                |        |
 | userId (FK) ------------+        |
 | title                   |        |
 | description             |        |
 | category                |        |
 | priority                |        |
 | dueDate                 |        |
 | expectedDuration        |        |
 | actualTrackedDuration   |        |
 | status                  |        |
 +-------------------------+        |
              | 1                   |
              |                     |
              | 0..N                |
 +-------------------------+        |
 |         Session         |        |
 +-------------------------+        |
 | _id (PK)                |        |
 | userId (FK) ---------------------+
 | taskId (FK, NULLABLE) --+
 | activityName            |
 | category                |
 | startTime               |
 | endTime                 |
 | durationSeconds         |
 | pausedDurationMillis    |
 | dateString              |
 | status                  |
 +-------------------------+
```

---

## 4. Android Room SQLite Mapping
- `TaskEntity` $\to$ Table `tasks` (PrimaryKey: `id` Long autoGenerate)
- `SessionEntity` $\to$ Table `sessions` (PrimaryKey: `id` Long autoGenerate)
- Indexed on `dateString` and `createdAt` for fast UI queries.
