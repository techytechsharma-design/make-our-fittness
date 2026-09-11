package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, duration: Int, calories: Int, sets: Int, reps: Int, weight: Float, distance: Float) -> Unit,
    estimateCalories: (category: String, duration: Int) -> Int
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Strength") }
    var durationText by remember { mutableStateOf("30") }
    var caloriesText by remember { mutableStateOf("180") }
    var setsText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var distanceText by remember { mutableStateOf("") }

    val categories = listOf("Strength", "Cardio", "HIIT", "Flexibility", "Sports")

    // Update calories auto-calculation when duration or category changes
    fun updateEstimatedCalories(newCategory: String, newDuration: String) {
        val mins = newDuration.toIntOrNull() ?: 0
        if (mins > 0) {
            val estimated = estimateCalories(newCategory, mins)
            caloriesText = estimated.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Workout",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("e.g. Bench Press, Jogging, Yoga") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workout_title_input")
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                                updateEstimatedCalories(category, durationText)
                            },
                            label = { Text(category) },
                            modifier = Modifier.testTag("cat_chip_$category")
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(3).forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                                updateEstimatedCalories(category, durationText)
                            },
                            label = { Text(category) },
                            modifier = Modifier.testTag("cat_chip_$category")
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = {
                            durationText = it
                            updateEstimatedCalories(selectedCategory, it)
                        },
                        label = { Text("Duration (mins)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("workout_duration_input")
                    )

                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { caloriesText = it },
                        label = { Text("Calories (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("workout_calories_input")
                    )
                }

                if (selectedCategory == "Strength") {
                    Text(
                        text = "Strength Details (Optional)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = setsText,
                            onValueChange = { setsText = it },
                            label = { Text("Sets") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = repsText,
                            onValueChange = { repsText = it },
                            label = { Text("Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            label = { Text("Kg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else if (selectedCategory == "Cardio") {
                    OutlinedTextField(
                        value = distanceText,
                        onValueChange = { distanceText = it },
                        label = { Text("Distance in km (Optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 15
                    val calories = caloriesText.toIntOrNull() ?: estimateCalories(selectedCategory, duration)
                    val sets = setsText.toIntOrNull() ?: 0
                    val reps = repsText.toIntOrNull() ?: 0
                    val weight = weightText.toFloatOrNull() ?: 0f
                    val distance = distanceText.toFloatOrNull() ?: 0f

                    onSave(
                        title.ifBlank { "$selectedCategory Workout" },
                        selectedCategory,
                        duration,
                        calories,
                        sets,
                        reps,
                        weight,
                        distance
                    )
                },
                modifier = Modifier.testTag("save_workout_btn")
            ) {
                Text("Save Workout")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_workout_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, mealType: String, calories: Int, protein: Float, carbs: Float, fat: Float, portion: String) -> Unit,
    initialMealType: String = "Lunch"
) {
    var name by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf(initialMealType) }
    var caloriesText by remember { mutableStateOf("") }
    var portionText by remember { mutableStateOf("") }
    var proteinText by remember { mutableStateOf("") }
    var carbsText by remember { mutableStateOf("") }
    var fatText by remember { mutableStateOf("") }

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snacks")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Meal / Food",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food / Dish Name") },
                    placeholder = { Text("e.g. Grilled Chicken, Oatmeal, Roti") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("meal_name_input")
                )

                Text(
                    text = "Meal Type",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    mealTypes.forEach { type ->
                        FilterChip(
                            selected = selectedMealType == type,
                            onClick = { selectedMealType = type },
                            label = { Text(type, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("type_chip_$type")
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { caloriesText = it },
                        label = { Text("Calories (kcal)*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("meal_calories_input")
                    )

                    OutlinedTextField(
                        value = portionText,
                        onValueChange = { portionText = it },
                        label = { Text("Portion Size") },
                        placeholder = { Text("1 bowl, 200g") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Macronutrients (Optional)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("meal_protein_input")
                    )

                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it },
                        label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("meal_carbs_input")
                    )

                    OutlinedTextField(
                        value = fatText,
                        onValueChange = { fatText = it },
                        label = { Text("Fat (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("meal_fat_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val calories = caloriesText.toIntOrNull() ?: 250
                    val protein = proteinText.toFloatOrNull() ?: 0f
                    val carbs = carbsText.toFloatOrNull() ?: 0f
                    val fat = fatText.toFloatOrNull() ?: 0f

                    onSave(
                        name.ifBlank { "Nutritious Food" },
                        selectedMealType,
                        calories,
                        protein,
                        carbs,
                        fat,
                        portionText
                    )
                },
                modifier = Modifier.testTag("save_meal_btn")
            ) {
                Text("Save Meal")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_meal_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditCalorieGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Daily Calorie Target",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter your target daily calorie intake budget:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    label = { Text("Daily Target (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_calories_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newGoal = goalText.toIntOrNull() ?: currentGoal
                    onSave(newGoal)
                },
                modifier = Modifier.testTag("save_goal_btn")
            ) {
                Text("Save Target")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_goal_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
