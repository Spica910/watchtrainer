package com.watchtrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.watchtrainer.data.repository.StatsPeriod
import com.watchtrainer.data.repository.WorkoutStats
import java.util.concurrent.TimeUnit

@Composable
fun StatsCard(stats: WorkoutStats) {
    Card(
        onClick = { /* No action */ },
        modifier = Modifier.fillMaxWidth(),
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            startBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
            endBackgroundColor = MaterialTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getPeriodText(stats.period) + " 통계",
                style = MaterialTheme.typography.caption1,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Main stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = stats.totalWorkouts.toString(),
                    label = "운동 횟수",
                    emoji = "🎯"
                )
                StatItem(
                    value = formatTime(stats.totalDuration),
                    label = "총 시간",
                    emoji = "⏱️"
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Secondary stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = formatNumber(stats.totalSteps),
                    label = "총 걸음",
                    emoji = "👣"
                )
                StatItem(
                    value = "${stats.totalCalories.toInt()}",
                    label = "칼로리",
                    emoji = "🔥"
                )
            }
            
            // Most frequent workout type
            stats.mostFrequentWorkoutType?.let { type ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "주요 운동: ${getWorkoutTypeName(type)}",
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    emoji: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.title3
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body2
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption3,
            color = MaterialTheme.colors.onSurfaceVariant
        )
    }
}

private fun getPeriodText(period: StatsPeriod): String {
    return when (period) {
        StatsPeriod.TODAY -> "오늘"
        StatsPeriod.WEEK -> "이번 주"
        StatsPeriod.MONTH -> "이번 달"
        StatsPeriod.ALL_TIME -> "전체"
    }
}

private fun formatTime(milliseconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}분"
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${number / 1_000_000}M"
        number >= 1_000 -> "${number / 1_000}K"
        else -> number.toString()
    }
}

private fun getWorkoutTypeName(type: com.watchtrainer.data.WorkoutType): String {
    return when (type) {
        com.watchtrainer.data.WorkoutType.WALKING -> "걷기"
        com.watchtrainer.data.WorkoutType.RUNNING -> "달리기"
        com.watchtrainer.data.WorkoutType.CYCLING -> "자전거"
        com.watchtrainer.data.WorkoutType.STRENGTH -> "근력운동"
        com.watchtrainer.data.WorkoutType.YOGA -> "요가"
        com.watchtrainer.data.WorkoutType.OTHER -> "기타"
    }
}