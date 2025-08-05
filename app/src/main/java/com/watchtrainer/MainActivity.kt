package com.watchtrainer

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.watchtrainer.data.WorkoutState
import com.watchtrainer.ui.screens.HomeScreen
import com.watchtrainer.ui.screens.WorkoutScreen
import com.watchtrainer.ui.screens.HistoryScreen
import com.watchtrainer.ui.screens.GoalsScreen
import com.watchtrainer.ui.screens.SettingsScreen
import com.watchtrainer.ui.screens.PlacesScreen
import com.watchtrainer.ui.theme.WatchTrainerTheme
import com.watchtrainer.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            WatchTrainerApp()
        }
    }
    
    private val mainViewModel: MainViewModel by lazy {
        MainViewModel(application)
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_STEM_PRIMARY -> {
                // Handle main button press
                handleMainButtonPress()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
    
    private fun handleMainButtonPress() {
        // Toggle workout state when main button is pressed
        when (mainViewModel.workoutState.value) {
            WorkoutState.IDLE -> mainViewModel.startWorkout()
            WorkoutState.ACTIVE -> mainViewModel.stopWorkout()
            WorkoutState.PAUSED -> mainViewModel.resumeWorkout()
        }
        
        // Vibrate for feedback
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}

@Composable
fun WatchTrainerApp() {
    WatchTrainerTheme {
        val navController = rememberSwipeDismissableNavController()
        val viewModel: MainViewModel = viewModel()
        
        Scaffold(
            timeText = {
                TimeText()
            },
            vignette = {
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            },
            positionIndicator = {
                PositionIndicator(
                    scalingLazyListState = viewModel.listState
                )
            }
        ) {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable("workout") {
                    WorkoutScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable("history") {
                    HistoryScreen(
                        navController = navController
                    )
                }
                composable("goals") {
                    GoalsScreen(
                        navController = navController
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable("places") {
                    PlacesScreen(
                        navController = navController
                    )
                }
            }
        }
    }
}