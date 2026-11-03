package com.example.data.model

data class UserPreferences(
    val name: String = "Aditya Dandugula",
    val email: String = "aditya.cs@university.edu",
    val studentId: String = "B.Tech CSE - 2026",
    val dailyGoalMinutes: Int = 120, // 2 hours default
    val vibrationEnabled: Boolean = true,
    val isDarkMode: Boolean = true
)
