package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // Cardio, Strength, HIIT, Flexibility, Sports
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val sets: Int = 0,
    val reps: Int = 0,
    val weightKg: Float = 0f,
    val distanceKm: Float = 0f,
    val dateString: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)
