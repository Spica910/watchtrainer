package com.watchtrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.watchtrainer.R
import com.watchtrainer.ui.components.HealthDataCard
import com.watchtrainer.ui.components.WeatherCard
import com.watchtrainer.ui.components.AiMessageCard
import com.watchtrainer.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val healthData by viewModel.healthData.collectAsState()
    val weatherData by viewModel.weatherData.collectAsState()
    val aiMessage by viewModel.aiMessage.collectAsState()
    val workoutState by viewModel.workoutState.collectAsState()
    
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        timeText = {
            if (!listState.isScrollInProgress) {
                TimeText()
            }
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                top = 32.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "운동 트레이너",
                    style = MaterialTheme.typography.title2,
                    textAlign = TextAlign.Center
                )
            }
            
            item {
                // Main workout button
                Button(
                    onClick = {
                        when (workoutState) {
                            com.watchtrainer.data.WorkoutState.IDLE -> {
                                viewModel.startWorkout()
                                navController.navigate("workout")
                            }
                            else -> navController.navigate("workout")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text(
                        text = when (workoutState) {
                            com.watchtrainer.data.WorkoutState.IDLE -> "운동 시작"
                            com.watchtrainer.data.WorkoutState.ACTIVE -> "운동 중"
                            com.watchtrainer.data.WorkoutState.PAUSED -> "일시정지"
                        },
                        style = MaterialTheme.typography.button
                    )
                }
            }
            
            item {
                HealthDataCard(healthData = healthData)
            }
            
            weatherData?.let { weather ->
                item {
                    WeatherCard(weatherData = weather)
                }
                
                item {
                    Button(
                        onClick = { navController.navigate("places") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.8f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📍 ")
                            Text("주변 운동 장소")
                        }
                    }
                }
            }
            
            if (aiMessage.isNotEmpty()) {
                item {
                    AiMessageCard(message = aiMessage)
                }
            }
            
            item {
                Button(
                    onClick = {
                        viewModel.requestAiGuidance()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("AI 코치 조언 받기")
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("goals") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        )
                    ) {
                        Text("목표")
                    }
                    Button(
                        onClick = { navController.navigate("history") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        )
                    ) {
                        Text("기록")
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                Chip(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ChipDefaults.chipColors(
                        backgroundColor = MaterialTheme.colors.surface
                    ),
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_manage),
                                contentDescription = "설정",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("설정")
                        }
                    }
                )
            }
        }
    }
}