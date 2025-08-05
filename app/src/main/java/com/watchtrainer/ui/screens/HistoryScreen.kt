package com.watchtrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import com.watchtrainer.data.WorkoutSession
import com.watchtrainer.data.repository.StatsPeriod
import com.watchtrainer.data.repository.WorkoutStats
import com.watchtrainer.ui.components.WorkoutHistoryCard
import com.watchtrainer.ui.components.StatsCard
import com.watchtrainer.viewmodels.HistoryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsState()
    val currentStats by viewModel.currentStats.collectAsState()
    var selectedPeriod by remember { mutableStateOf(StatsPeriod.WEEK) }
    
    LaunchedEffect(selectedPeriod) {
        viewModel.loadStats(selectedPeriod)
    }
    
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                    text = "운동 기록",
                    style = MaterialTheme.typography.title2,
                    textAlign = TextAlign.Center
                )
            }
            
            // Period selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PeriodChip(
                        selected = selectedPeriod == StatsPeriod.TODAY,
                        onClick = { selectedPeriod = StatsPeriod.TODAY },
                        label = "오늘"
                    )
                    PeriodChip(
                        selected = selectedPeriod == StatsPeriod.WEEK,
                        onClick = { selectedPeriod = StatsPeriod.WEEK },
                        label = "주간"
                    )
                    PeriodChip(
                        selected = selectedPeriod == StatsPeriod.MONTH,
                        onClick = { selectedPeriod = StatsPeriod.MONTH },
                        label = "월간"
                    )
                }
            }
            
            // Statistics
            currentStats?.let { stats ->
                item {
                    StatsCard(stats = stats)
                }
            }
            
            // Workout history
            item {
                Text(
                    text = "최근 운동",
                    style = MaterialTheme.typography.caption1,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(workouts.take(10)) { workout ->
                WorkoutHistoryCard(
                    workout = workout,
                    onClick = {
                        // Navigate to workout detail
                    }
                )
            }
            
            if (workouts.isEmpty()) {
                item {
                    Text(
                        text = "아직 운동 기록이 없어요",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Chip(
        onClick = onClick,
        colors = ChipDefaults.chipColors(
            backgroundColor = if (selected) 
                MaterialTheme.colors.primary 
            else 
                MaterialTheme.colors.surface
        ),
        modifier = Modifier.height(32.dp),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.caption2
            )
        }
    )
}