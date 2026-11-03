# TIME LOOM — REST API Specification
**Academic B.Tech Computer Science Engineering Deliverable**  
**Base URL:** `http://localhost:5000/api`  
**Data Format:** JSON (`application/json`)  

---

## 1. Authentication Endpoints
### `POST /api/auth/register`
Creates a new user record in MongoDB.
- **Request Body:**
  ```json
  {
    "name": "Aditya Dandugula",
    "email": "aditya.cs@university.edu",
    "password": "Password123"
  }
  ```
- **Response (201 Created):**
  ```json
  {
    "success": true,
    "data": {
      "id": "672439810a9c8f2b1c4e789a",
      "name": "Aditya Dandugula",
      "email": "aditya.cs@university.edu",
      "dailyGoalMinutes": 120
    }
  }
  ```

### `POST /api/auth/login`
Authenticates user and returns session token.
- **Request Body:**
  ```json
  {
    "email": "aditya.cs@university.edu",
    "password": "Password123"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Login successful",
    "token": "jwt_token_string",
    "user": { ... }
  }
  ```

---

## 2. Tasks Endpoints
### `GET /api/tasks`
Retrieves tasks with optional query filtering (Algorithm 2).
- **Query Parameters:**
  - `query`: String (search in title and description)
  - `category`: String (`Study`, `Coding`, `Project`, `Research`, etc.)
  - `status`: String (`Pending`, `In Progress`, `Completed`)
  - `priority`: String (`Low`, `Medium`, `High`, `Urgent`)
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "count": 2,
    "data": [
      {
        "_id": "67243a010a9c8f2b1c4e789b",
        "title": "Data Structures: Graph BFS & DFS",
        "description": "Lab assignment implementation",
        "category": "Coding",
        "priority": "High",
        "dueDate": "2026-11-05",
        "expectedDuration": 60,
        "actualTrackedDuration": 45,
        "status": "In Progress"
      }
    ]
  }
  ```

### `POST /api/tasks`
Creates a new task.
- **Request Body:**
  ```json
  {
    "title": "Operating Systems: Semaphore Program",
    "description": "Producer-consumer synchronization in C",
    "category": "Coding",
    "priority": "Urgent",
    "dueDate": "2026-11-06",
    "expectedDuration": 90
  }
  ```
- **Response (201 Created):**
  ```json
  { "success": true, "data": { "_id": "...", "status": "Pending" } }
  ```

### `PUT /api/tasks/:id`
Updates an existing task by ObjectId.

### `DELETE /api/tasks/:id`
Deletes a task by ObjectId.

### `PATCH /api/tasks/:id/status`
Updates task status enum (`Pending`, `In Progress`, `Completed`).
- **Request Body:** `{ "status": "Completed" }`

---

## 3. Session Endpoints
### `POST /api/sessions/start`
Initializes a new tracking session with current timestamp.
- **Request Body:**
  ```json
  {
    "taskId": "67243a010a9c8f2b1c4e789b",
    "activityName": "Graph BFS Algorithm Sprint",
    "category": "Coding"
  }
  ```
- **Response (201 Created):**
  ```json
  {
    "success": true,
    "data": {
      "_id": "67243c550a9c8f2b1c4e78a1",
      "startTime": 1730592000000,
      "status": "Active"
    }
  }
  ```

### `PATCH /api/sessions/:id/stop`
Stops session, computes active duration via Algorithm 1, updates associated task.
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "data": {
      "_id": "67243c550a9c8f2b1c4e78a1",
      "durationSeconds": 2700,
      "status": "Completed"
    }
  }
  ```

### `GET /api/sessions`
Returns history of all recorded sessions.

---

## 4. Reports & Analytics Endpoints
### `GET /api/reports/daily`
Aggregates sessions into daily minutes map (Algorithm 3).
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "data": {
      "2026-11-02": 135,
      "2026-11-01": 90
    }
  }
  ```

### `GET /api/reports/heatmap?year=2026&month=11`
Generates month grid with discrete intensity levels 0–4 (Algorithm 4).
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "year": 2026,
    "month": 11,
    "data": [
      { "dateString": "2026-11-01", "dayOfMonth": 1, "trackedMinutes": 90, "intensityLevel": 3 },
      { "dateString": "2026-11-02", "dayOfMonth": 2, "trackedMinutes": 135, "intensityLevel": 4 }
    ]
  }
  ```

### `GET /api/reports/weekly`
Returns 7-day Monday–Sunday analytics (Algorithm 5).

### `GET /api/reports/productivity?dailyGoal=120`
Returns rule-based productivity score breakdown (Algorithm 6).
- **Response (200 OK):**
  ```json
  {
    "success": true,
    "data": {
      "overallScore": 84,
      "taskCompletionScore": 80,
      "focusTimeScore": 100,
      "consistencyScore": 71,
      "explanation": "Exceptional academic consistency and focus execution."
    }
  }
  ```
