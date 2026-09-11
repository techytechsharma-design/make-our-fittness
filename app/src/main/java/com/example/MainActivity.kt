package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.FitnessDatabase
import com.example.data.repository.FitnessRepository
import com.example.ui.FitnessViewModel
import com.example.ui.FitnessViewModelFactory
import com.example.ui.screens.FitnessApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = FitnessDatabase.getInstance(applicationContext)
    val repository = FitnessRepository(
      workoutDao = database.workoutDao(),
      mealDao = database.mealDao(),
      waterDao = database.waterDao()
    )

    setContent {
      MyApplicationTheme {
        val viewModel: FitnessViewModel = viewModel(
          factory = FitnessViewModelFactory(repository)
        )
        FitnessApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
