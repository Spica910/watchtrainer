package com.watchtrainer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.watchtrainer.data.WorkoutSession
import com.watchtrainer.data.WorkoutType
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun WorkoutHistoryCard(
    workout: WorkoutSession,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundPainter = CardDefaults.cardBackgroundPainter()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = getWorkoutTypeEmoji(workout.workoutType) + " " + getWorkoutTypeName(workout.workoutType),
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = formatDate(workout.startTime),
                        style = MaterialTheme.typography.caption2,
                        color = MaterialTheme.colors.onSurfaceVariant
                    )
                }
                
                Text(
                    text = formatDuration(workout.endTime?.minus(workout.startTime) ?: 0),
                    style = MaterialTheme.typography.caption1
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WorkoutMetric(
                    value = workout.totalSteps.toString(),
                    label = "걸음"
                )
                WorkoutMetric(
                    value = "${workout.averageHeartRate}",
                    label = "평균 BPM"
                )
                WorkoutMetric(
                    value = "${workout.caloriesBurned.toInt()}",
                    label = "칼로리"
                )
            }
        }
    }
}

@Composable
private fun WorkoutMetric(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.caption1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption3,
            color = MaterialTheme.colors.onSurfaceVariant
        )
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

private fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}

private fun formatDuration(milliseconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    
    return if (hours > 0) {
        "${hours}시간 ${minutes}분"
    } else {
        "${minutes}분"
    }
}