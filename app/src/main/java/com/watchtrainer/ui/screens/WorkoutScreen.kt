package com.watchtrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import com.watchtrainer.data.WorkoutState
import com.watchtrainer.data.WorkoutType
import com.watchtrainer.ui.components.CircularMetric
import com.watchtrainer.viewmodels.MainViewModel
import java.util.concurrent.TimeUnit

@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val workoutState by viewModel.workoutState.collectAsState()
    val healthData by viewModel.healthData.collectAsState()
    val aiMessage by viewModel.aiMessage.collectAsState()
    
    var elapsedTime by remember { mutableStateOf(0L) }
    var showWorkoutTypeSelector by remember { mutableStateOf(false) }
    
    LaunchedEffect(workoutState) {
        if (workoutState == WorkoutState.ACTIVE) {
            while (workoutState == WorkoutState.ACTIVE) {
                kotlinx.coroutines.delay(1000)
                elapsedTime += 1000
            }
        }
    }
    
    if (showWorkoutTypeSelector) {
        WorkoutTypeDialog(
            onDismiss = { showWorkoutTypeSelector = false },
            onTypeSelected = { type ->
                viewModel.setWorkoutType(type)
                showWorkoutTypeSelector = false
            }
        )
    }
    
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Workout type and timer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Chip(
                    onClick = { showWorkoutTypeSelector = true },
                    label = { 
                        Text(
                            text = getWorkoutTypeName(viewModel.currentWorkoutType),
                            style = MaterialTheme.typography.caption1
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatTime(elapsedTime),
                    style = MaterialTheme.typography.display2,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Health metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularMetric(
                    value = healthData.heartRate.toString(),
                    label = "BPM",
                    progress = healthData.heartRate / 200f
                )
                CircularMetric(
                    value = healthData.steps.toString(),
                    label = "걸음",
                    progress = healthData.steps / 10000f
                )
            }
            
            // AI message
            if (aiMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundPainter = CardDefaults.cardBackgroundPainter()
                ) {
                    Text(
                        text = aiMessage,
                        style = MaterialTheme.typography.caption1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            
            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (workoutState) {
                    WorkoutState.ACTIVE -> {
                        Button(
                            onClick = { viewModel.stopWorkout() },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.secondary
                            )
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_media_pause),
                                contentDescription = "일시정지"
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.endWorkout()
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error
                            )
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_delete),
                                contentDescription = "종료"
                            )
                        }
                    }
                    WorkoutState.PAUSED -> {
                        Button(
                            onClick = { viewModel.resumeWorkout() },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary
                            )
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_media_play),
                                contentDescription = "재개"
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.endWorkout()
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error
                            )
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_delete),
                                contentDescription = "종료"
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Composable
fun WorkoutTypeDialog(
    onDismiss: () -> Unit,
    onTypeSelected: (WorkoutType) -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundPainter = CardDefaults.cardBackgroundPainter()
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxHeight(0.8f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    Text(
                        text = "운동 선택",
                        style = MaterialTheme.typography.title3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                items(WorkoutType.values().toList()) { type ->
                    Chip(
                        onClick = { onTypeSelected(type) },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = getWorkoutTypeEmoji(type),
                                    style = MaterialTheme.typography.title3
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = getWorkoutTypeName(type),
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun getWorkoutTypeEmoji(type: WorkoutType): String {
    return when (type) {
        WorkoutType.WALKING -> "🚶"
        WorkoutType.RUNNING -> "🏃"
        WorkoutType.CYCLING -> "🚴"
        WorkoutType.STRENGTH -> "💪"
        WorkoutType.YOGA -> "🧘"
        WorkoutType.OTHER -> "⭐"
    }
}

private fun getWorkoutTypeName(type: WorkoutType): String {
    return when (type) {
        WorkoutType.WALKING -> "걷기"
        WorkoutType.RUNNING -> "달리기"
        WorkoutType.CYCLING -> "자전거"
        WorkoutType.STRENGTH -> "근력운동"
        WorkoutType.YOGA -> "요가"
        WorkoutType.OTHER -> "기타"
    }
}