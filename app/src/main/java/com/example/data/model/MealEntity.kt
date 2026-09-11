package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val mealType: String, // Breakfast, Lunch, Dinner, Snacks
    val calories: Int,
    val proteinGrams: Float = 0f,
    val carbsGrams: Float = 0f,
    val fatGrams: Float = 0f,
    val portionDescription: String = "",
    val dateString: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)
