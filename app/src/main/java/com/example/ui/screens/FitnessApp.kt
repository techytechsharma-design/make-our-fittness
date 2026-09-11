package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.FitnessViewModel
import com.example.ui.components.AddMealDialog
import com.example.ui.components.AddWorkoutDialog
import com.example.ui.components.EditCalorieGoalDialog
import com.example.ui.theme.CalorieBurnOrange
import com.example.ui.theme.CalorieConsumedGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessApp(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val showAddWorkout by viewModel.showAddWorkoutDialog.collectAsStateWithLifecycle()
    val showAddMeal by viewModel.showAddMealDialog.collectAsStateWithLifecycle()
    val showGoalDialog by viewModel.showGoalDialog.collectAsStateWithLifecycle()

    val dateLabel = viewModel.getFormattedSelectedDate()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    // Small calorie status pill in top bar
                    Text(
                        text = "${summary.consumed} / ${summary.goal} kcal",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("bottom_nav_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = stringResource(R.string.nav_today)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_today)) },
                    modifier = Modifier.testTag("nav_tab_today")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = stringResource(R.string.nav_workouts)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_workouts)) },
                    modifier = Modifier.testTag("nav_tab_workouts")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = stringResource(R.string.nav_diet)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_diet)) },
                    modifier = Modifier.testTag("nav_tab_diet")
                )
            }
        },
        floatingActionButton = {
            // Contextual FAB based on active tab
            when (selectedTab) {
                1 -> {
                    FloatingActionButton(
                        onClick = { viewModel.showAddWorkoutDialog.value = true },
                        containerColor = CalorieBurnOrange,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.testTag("fab_add_workout")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Workout")
                    }
                }
                2 -> {
                    FloatingActionButton(
                        onClick = { viewModel.showAddMealDialog.value = true },
                        containerColor = CalorieConsumedGreen,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("fab_add_meal")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Meal")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    dateLabel = dateLabel,
                    summary = summary,
                    workouts = workouts,
                    meals = meals,
                    onPreviousDay = { viewModel.goToPreviousDay() },
                    onNextDay = { viewModel.goToNextDay() },
                    onToday = { viewModel.goToToday() },
                    onEditGoal = { viewModel.showGoalDialog.value = true },
                    onAddWorkoutClick = { viewModel.showAddWorkoutDialog.value = true },
                    onAddMealClick = { viewModel.showAddMealDialog.value = true },
                    onAddWater = { viewModel.addWater(it) },
                    onUndoWater = { viewModel.undoLastWater() },
                    onNavigateToWorkouts = { selectedTab = 1 },
                    onNavigateToDiet = { selectedTab = 2 }
                )
                1 -> WorkoutsScreen(
                    dateLabel = dateLabel,
                    workouts = workouts,
                    onPreviousDay = { viewModel.goToPreviousDay() },
                    onNextDay = { viewModel.goToNextDay() },
                    onToday = { viewModel.goToToday() },
                    onAddWorkoutClick = { viewModel.showAddWorkoutDialog.value = true },
                    onQuickAddWorkout = { preset ->
                        viewModel.addWorkout(
                            title = preset.title,
                            category = preset.category,
                            durationMinutes = preset.durationMinutes,
                            caloriesBurned = preset.caloriesBurned
                        )
                    },
                    onDeleteWorkout = { viewModel.deleteWorkout(it) }
                )
                2 -> DietScreen(
                    dateLabel = dateLabel,
                    summary = summary,
                    meals = meals,
                    onPreviousDay = { viewModel.goToPreviousDay() },
                    onNextDay = { viewModel.goToNextDay() },
                    onToday = { viewModel.goToToday() },
                    onAddMealClick = { viewModel.showAddMealDialog.value = true },
                    onQuickAddFood = { preset ->
                        viewModel.addMeal(
                            name = preset.name,
                            mealType = preset.mealType,
                            calories = preset.calories,
                            proteinGrams = preset.protein,
                            carbsGrams = preset.carbs,
                            fatGrams = preset.fat,
                            portionDescription = preset.portion
                        )
                    },
                    onDeleteMeal = { viewModel.deleteMeal(it) }
                )
            }

            // Dialogs
            if (showAddWorkout) {
                AddWorkoutDialog(
                    onDismiss = { viewModel.showAddWorkoutDialog.value = false },
                    onSave = { title, category, duration, calories, sets, reps, weight, distance ->
                        viewModel.addWorkout(
                            title = title,
                            category = category,
                            durationMinutes = duration,
                            caloriesBurned = calories,
                            sets = sets,
                            reps = reps,
                            weightKg = weight,
                            distanceKm = distance
                        )
                        viewModel.showAddWorkoutDialog.value = false
                    },
                    estimateCalories = { category, duration ->
                        viewModel.estimateCaloriesBurned(category, duration)
                    }
                )
            }

            if (showAddMeal) {
                AddMealDialog(
                    onDismiss = { viewModel.showAddMealDialog.value = false },
                    onSave = { name, mealType, calories, protein, carbs, fat, portion ->
                        viewModel.addMeal(
                            name = name,
                            mealType = mealType,
                            calories = calories,
                            proteinGrams = protein,
                            carbsGrams = carbs,
                            fatGrams = fat,
                            portionDescription = portion
                        )
                        viewModel.showAddMealDialog.value = false
                    }
                )
            }

            if (showGoalDialog) {
                EditCalorieGoalDialog(
                    currentGoal = summary.goal,
                    onDismiss = { viewModel.showGoalDialog.value = false },
                    onSave = { newGoal ->
                        viewModel.setCalorieGoal(newGoal)
                        viewModel.showGoalDialog.value = false
                    }
                )
            }
        }
    }
}
