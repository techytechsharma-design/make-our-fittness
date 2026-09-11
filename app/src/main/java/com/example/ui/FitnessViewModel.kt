package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.MealEntity
import com.example.data.model.WaterLogEntity
import com.example.data.model.WorkoutEntity
import com.example.data.repository.FitnessRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class QuickWorkoutPreset(
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val iconName: String = ""
)

data class QuickFoodPreset(
    val name: String,
    val mealType: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val portion: String
)

data class CalorieSummary(
    val goal: Int = 2200,
    val consumed: Int = 0,
    val burned: Int = 0,
    val remaining: Int = 2200,
    val net: Int = 0,
    val progressFraction: Float = 0f,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val totalWaterMl: Int = 0,
    val waterGoalMl: Int = 2500,
    val totalWorkoutMinutes: Int = 0
)

class FitnessViewModel(
    private val repository: FitnessRepository
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())

    private val _currentCalendar = MutableStateFlow(Calendar.getInstance())
    val currentCalendar: StateFlow<Calendar> = _currentCalendar.asStateFlow()

    private val _selectedDateString = MutableStateFlow(dateFormatter.format(Date()))
    val selectedDateString: StateFlow<String> = _selectedDateString.asStateFlow()

    private val _calorieGoal = MutableStateFlow(2200)
    val calorieGoal: StateFlow<Int> = _calorieGoal.asStateFlow()

    private val _waterGoal = MutableStateFlow(2500)
    val waterGoal: StateFlow<Int> = _waterGoal.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val workouts: StateFlow<List<WorkoutEntity>> = _selectedDateString
        .flatMapLatest { date -> repository.getWorkoutsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val meals: StateFlow<List<MealEntity>> = _selectedDateString
        .flatMapLatest { date -> repository.getMealsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val waterLogs: StateFlow<List<WaterLogEntity>> = _selectedDateString
        .flatMapLatest { date -> repository.getWaterLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<CalorieSummary> = combine(
        workouts,
        meals,
        waterLogs,
        _calorieGoal,
        _waterGoal
    ) { workoutList, mealList, waterList, goal, waterTarget ->
        val consumed = mealList.sumOf { it.calories }
        val burned = workoutList.sumOf { it.caloriesBurned }
        val minutes = workoutList.sumOf { it.durationMinutes }
        val protein = mealList.fold(0f) { acc, m -> acc + m.proteinGrams }
        val carbs = mealList.fold(0f) { acc, m -> acc + m.carbsGrams }
        val fat = mealList.fold(0f) { acc, m -> acc + m.fatGrams }
        val water = waterList.sumOf { it.amountMl }

        val remaining = (goal - consumed + burned).coerceAtLeast(0)
        val net = consumed - burned
        val progress = if (goal > 0) (consumed.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f

        CalorieSummary(
            goal = goal,
            consumed = consumed,
            burned = burned,
            remaining = remaining,
            net = net,
            progressFraction = progress,
            totalProtein = protein,
            totalCarbs = carbs,
            totalFat = fat,
            totalWaterMl = water,
            waterGoalMl = waterTarget,
            totalWorkoutMinutes = minutes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalorieSummary())

    // UI Dialog States
    val showAddWorkoutDialog = MutableStateFlow(false)
    val showAddMealDialog = MutableStateFlow(false)
    val showGoalDialog = MutableStateFlow(false)

    // Workout category filter: All, Cardio, Strength, HIIT, Flexibility
    val workoutCategoryFilter = MutableStateFlow("All")

    // Meal type filter: All, Breakfast, Lunch, Dinner, Snacks
    val mealTypeFilter = MutableStateFlow("All")

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty(_selectedDateString.value)
        }
    }

    fun setSelectedDate(calendar: Calendar) {
        _currentCalendar.value = calendar
        _selectedDateString.value = dateFormatter.format(calendar.time)
    }

    fun goToPreviousDay() {
        val cal = (_currentCalendar.value.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        setSelectedDate(cal)
    }

    fun goToNextDay() {
        val cal = (_currentCalendar.value.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        setSelectedDate(cal)
    }

    fun goToToday() {
        val cal = Calendar.getInstance()
        setSelectedDate(cal)
    }

    fun getFormattedSelectedDate(): String {
        val todayStr = dateFormatter.format(Date())
        val selectedStr = _selectedDateString.value

        val calYesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterdayStr = dateFormatter.format(calYesterday.time)

        return when (selectedStr) {
            todayStr -> "Today, " + SimpleDateFormat("dd MMM", Locale.getDefault()).format(_currentCalendar.value.time)
            yesterdayStr -> "Yesterday, " + SimpleDateFormat("dd MMM", Locale.getDefault()).format(_currentCalendar.value.time)
            else -> displayDateFormatter.format(_currentCalendar.value.time)
        }
    }

    fun setCalorieGoal(newGoal: Int) {
        if (newGoal > 500) {
            _calorieGoal.value = newGoal
        }
    }

    fun addWorkout(
        title: String,
        category: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        sets: Int = 0,
        reps: Int = 0,
        weightKg: Float = 0f,
        distanceKm: Float = 0f
    ) {
        viewModelScope.launch {
            repository.insertWorkout(
                WorkoutEntity(
                    title = title.ifBlank { "Workout" },
                    category = category,
                    durationMinutes = durationMinutes,
                    caloriesBurned = caloriesBurned,
                    sets = sets,
                    reps = reps,
                    weightKg = weightKg,
                    distanceKm = distanceKm,
                    dateString = _selectedDateString.value
                )
            )
        }
    }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    fun addMeal(
        name: String,
        mealType: String,
        calories: Int,
        proteinGrams: Float = 0f,
        carbsGrams: Float = 0f,
        fatGrams: Float = 0f,
        portionDescription: String = ""
    ) {
        viewModelScope.launch {
            repository.insertMeal(
                MealEntity(
                    name = name.ifBlank { "Meal" },
                    mealType = mealType,
                    calories = calories,
                    proteinGrams = proteinGrams,
                    carbsGrams = carbsGrams,
                    fatGrams = fatGrams,
                    portionDescription = portionDescription,
                    dateString = _selectedDateString.value
                )
            )
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWater(_selectedDateString.value, amountMl)
        }
    }

    fun undoLastWater() {
        viewModelScope.launch {
            repository.removeLastWater(_selectedDateString.value)
        }
    }

    fun estimateCaloriesBurned(category: String, durationMins: Int): Int {
        val ratePerMin = when (category) {
            "Cardio" -> 9.5f
            "Strength" -> 6.0f
            "HIIT" -> 11.5f
            "Flexibility" -> 4.5f
            "Sports" -> 8.5f
            else -> 7.0f
        }
        return (ratePerMin * durationMins).toInt().coerceAtLeast(10)
    }

    companion object {
        val WORKOUT_PRESETS = listOf(
            QuickWorkoutPreset("Outdoor Running", "Cardio", 30, 290),
            QuickWorkoutPreset("Weight Lifting", "Strength", 45, 250),
            QuickWorkoutPreset("Cycling", "Cardio", 30, 270),
            QuickWorkoutPreset("HIIT Cardio Blast", "HIIT", 20, 230),
            QuickWorkoutPreset("Morning Yoga & Stretch", "Flexibility", 25, 110),
            QuickWorkoutPreset("Brisk Walking", "Cardio", 40, 180),
            QuickWorkoutPreset("Swimming Laps", "Cardio", 30, 310),
            QuickWorkoutPreset("Pushups & Core Abs", "Strength", 20, 160)
        )

        val FOOD_PRESETS = listOf(
            QuickFoodPreset("Oatmeal & Banana", "Breakfast", 320, 12f, 54f, 7f, "1 bowl"),
            QuickFoodPreset("Eggs & Whole Wheat Toast", "Breakfast", 330, 20f, 26f, 14f, "2 eggs + 2 toasts"),
            QuickFoodPreset("Grilled Chicken & Brown Rice", "Lunch", 540, 44f, 55f, 12f, "1 plate"),
            QuickFoodPreset("Dal, Roti & Salad", "Lunch", 410, 17f, 62f, 9f, "2 rotis + 1 bowl dal"),
            QuickFoodPreset("Paneer Bhurji & Roti", "Dinner", 460, 24f, 36f, 22f, "1 serving"),
            QuickFoodPreset("Whey Protein Shake", "Snacks", 170, 27f, 3f, 2f, "1 scoop"),
            QuickFoodPreset("Apple with Almonds", "Snacks", 190, 5f, 25f, 9f, "1 apple + 10 almonds"),
            QuickFoodPreset("Greek Yogurt & Honey", "Snacks", 210, 16f, 22f, 5f, "1 cup")
        )
    }
}

class FitnessViewModelFactory(
    private val repository: FitnessRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FitnessViewModel::class.java)) {
            return FitnessViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
