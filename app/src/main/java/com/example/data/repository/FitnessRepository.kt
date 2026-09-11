package com.example.data.repository

import com.example.data.db.MealDao
import com.example.data.db.WaterDao
import com.example.data.db.WorkoutDao
import com.example.data.model.MealEntity
import com.example.data.model.WaterLogEntity
import com.example.data.model.WorkoutEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FitnessRepository(
    private val workoutDao: WorkoutDao,
    private val mealDao: MealDao,
    private val waterDao: WaterDao
) {
    fun getWorkoutsForDate(dateString: String): Flow<List<WorkoutEntity>> =
        workoutDao.getWorkoutsForDate(dateString)

    fun getMealsForDate(dateString: String): Flow<List<MealEntity>> =
        mealDao.getMealsForDate(dateString)

    fun getWaterLogsForDate(dateString: String): Flow<List<WaterLogEntity>> =
        waterDao.getWaterLogsForDate(dateString)

    suspend fun insertWorkout(workout: WorkoutEntity) =
        workoutDao.insertWorkout(workout)

    suspend fun deleteWorkout(workout: WorkoutEntity) =
        workoutDao.deleteWorkout(workout)

    suspend fun insertMeal(meal: MealEntity) =
        mealDao.insertMeal(meal)

    suspend fun deleteMeal(meal: MealEntity) =
        mealDao.deleteMeal(meal)

    suspend fun addWater(dateString: String, amountMl: Int) =
        waterDao.insertWater(
            WaterLogEntity(
                amountMl = amountMl,
                dateString = dateString
            )
        )

    suspend fun removeLastWater(dateString: String) =
        waterDao.removeLastWaterForDate(dateString)

    suspend fun seedInitialDataIfEmpty(todayDateString: String) {
        val currentWorkouts = workoutDao.getAllWorkouts().first()
        val currentMeals = mealDao.getAllMeals().first()
        if (currentWorkouts.isEmpty() && currentMeals.isEmpty()) {
            // Seed workouts for today
            workoutDao.insertWorkout(
                WorkoutEntity(
                    title = "Morning Jog",
                    category = "Cardio",
                    durationMinutes = 30,
                    caloriesBurned = 280,
                    distanceKm = 4.2f,
                    dateString = todayDateString
                )
            )
            workoutDao.insertWorkout(
                WorkoutEntity(
                    title = "Upper Body Dumbbells",
                    category = "Strength",
                    durationMinutes = 40,
                    caloriesBurned = 220,
                    sets = 4,
                    reps = 12,
                    weightKg = 16f,
                    dateString = todayDateString
                )
            )

            // Seed meals for today
            mealDao.insertMeal(
                MealEntity(
                    name = "Oatmeal with Berries & Almonds",
                    mealType = "Breakfast",
                    calories = 340,
                    proteinGrams = 14f,
                    carbsGrams = 52f,
                    fatGrams = 8f,
                    portionDescription = "1 medium bowl",
                    dateString = todayDateString
                )
            )
            mealDao.insertMeal(
                MealEntity(
                    name = "Grilled Chicken Rice Bowl",
                    mealType = "Lunch",
                    calories = 580,
                    proteinGrams = 42f,
                    carbsGrams = 60f,
                    fatGrams = 14f,
                    portionDescription = "1 standard serving",
                    dateString = todayDateString
                )
            )
            mealDao.insertMeal(
                MealEntity(
                    name = "Whey Protein Shake",
                    mealType = "Snacks",
                    calories = 170,
                    proteinGrams = 26f,
                    carbsGrams = 4f,
                    fatGrams = 2f,
                    portionDescription = "1 scoop with water",
                    dateString = todayDateString
                )
            )

            // Seed some water
            waterDao.insertWater(
                WaterLogEntity(
                    amountMl = 500,
                    dateString = todayDateString
                )
            )
            waterDao.insertWater(
                WaterLogEntity(
                    amountMl = 250,
                    dateString = todayDateString
                )
            )
        }
    }
}
