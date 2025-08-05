package com.watchtrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.watchtrainer.data.database.GoalType
import com.watchtrainer.data.database.WorkoutGoalEntity

@Composable
fun GoalProgressCard(
    goal: WorkoutGoalEntity,
    onComplete: () -> Unit
) {
    val progress = (goal.currentValue / goal.targetValue).coerceIn(0f, 1f)
    val isCompleted = progress >= 1f
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            startBackgroundColor = if (isCompleted) 
                MaterialTheme.colors.secondary.copy(alpha = 0.2f)
            else 
                MaterialTheme.colors.surface,
            endBackgroundColor = MaterialTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getGoalTypeEmoji(goal.goalType) + " " + getGoalTypeName(goal.goalType),
                    style = MaterialTheme.typography.body2
                )
                
                if (isCompleted) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.title3,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                indicatorColor = if (isCompleted) 
                    MaterialTheme.colors.secondary 
                else 
                    MaterialTheme.colors.primary,
                trackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${getGoalUnit(goal.goalType)}",
                    style = MaterialTheme.typography.caption1
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.caption1,
                    color = if (isCompleted) 
                        MaterialTheme.colors.secondary 
                    else 
                        MaterialTheme.colors.primary
                )
            }
            
            goal.deadline?.let { deadline ->
                val daysLeft = ((deadline.time - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
                if (daysLeft > 0) {
                    Text(
                        text = "남은 기간: ${daysLeft}일",
                        style = MaterialTheme.typography.caption2,
                        color = MaterialTheme.colors.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun getGoalTypeEmoji(type: GoalType): String {
    return when (type) {
        GoalType.DAILY_STEPS, GoalType.WEEKLY_STEPS -> "👣"
        GoalType.DAILY_CALORIES, GoalType.WEEKLY_CALORIES -> "🔥"
        GoalType.DAILY_ACTIVE_MINUTES, GoalType.WEEKLY_ACTIVE_MINUTES -> "⏱️"
        GoalType.MONTHLY_WORKOUTS -> "💪"
    }
}

private fun getGoalTypeName(type: GoalType): String {
    return when (type) {
        GoalType.DAILY_STEPS -> "일일 걸음"
        GoalType.WEEKLY_STEPS -> "주간 걸음"
        GoalType.DAILY_CALORIES -> "일일 칼로리"
        GoalType.WEEKLY_CALORIES -> "주간 칼로리"
        GoalType.DAILY_ACTIVE_MINUTES -> "일일 활동"
        GoalType.WEEKLY_ACTIVE_MINUTES -> "주간 활동"
        GoalType.MONTHLY_WORKOUTS -> "월간 운동"
    }
}

private fun getGoalUnit(type: GoalType): String {
    return when (type) {
        GoalType.DAILY_STEPS, GoalType.WEEKLY_STEPS -> "걸음"
        GoalType.DAILY_CALORIES, GoalType.WEEKLY_CALORIES -> "kcal"
        GoalType.DAILY_ACTIVE_MINUTES, GoalType.WEEKLY_ACTIVE_MINUTES -> "분"
        GoalType.MONTHLY_WORKOUTS -> "회"
    }
}