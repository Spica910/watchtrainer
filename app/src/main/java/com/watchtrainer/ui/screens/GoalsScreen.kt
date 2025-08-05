package com.watchtrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import com.watchtrainer.data.database.GoalType
import com.watchtrainer.data.database.WorkoutGoalEntity
import com.watchtrainer.ui.components.GoalProgressCard
import com.watchtrainer.viewmodels.GoalsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GoalsScreen(
    navController: NavController,
    viewModel: GoalsViewModel = viewModel()
) {
    val activeGoals by viewModel.activeGoals.collectAsState()
    var showAddGoal by remember { mutableStateOf(false) }
    
    if (showAddGoal) {
        AddGoalDialog(
            onDismiss = { showAddGoal = false },
            onConfirm = { goalType, targetValue ->
                viewModel.createGoal(goalType, targetValue)
                showAddGoal = false
            }
        )
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
                    text = "운동 목표",
                    style = MaterialTheme.typography.title2,
                    textAlign = TextAlign.Center
                )
            }
            
            items(activeGoals) { goal ->
                GoalProgressCard(
                    goal = goal,
                    onComplete = { viewModel.completeGoal(goal) }
                )
            }
            
            item {
                Button(
                    onClick = { showAddGoal = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("새 목표 추가")
                }
            }
            
            if (activeGoals.isEmpty()) {
                item {
                    Text(
                        text = "아직 설정된 목표가 없어요\n목표를 추가해보세요!",
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
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (GoalType, Float) -> Unit
) {
    var selectedGoalType by remember { mutableStateOf(GoalType.DAILY_STEPS) }
    var targetValue by remember { mutableStateOf("") }
    
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundPainter = CardDefaults.cardBackgroundPainter()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "목표 설정",
                    style = MaterialTheme.typography.title3
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Goal type selector
                GoalTypeSelector(
                    selectedType = selectedGoalType,
                    onTypeSelected = { selectedGoalType = it }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Target value input
                Text(
                    text = "목표값: ${targetValue.ifEmpty { "0" }} ${getGoalUnit(selectedGoalType)}",
                    style = MaterialTheme.typography.body2
                )
                
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CompactChip(
                        onClick = {
                            targetValue = when (selectedGoalType) {
                                GoalType.DAILY_STEPS, GoalType.WEEKLY_STEPS -> "5000"
                                GoalType.DAILY_CALORIES, GoalType.WEEKLY_CALORIES -> "300"
                                GoalType.DAILY_ACTIVE_MINUTES, GoalType.WEEKLY_ACTIVE_MINUTES -> "30"
                                GoalType.MONTHLY_WORKOUTS -> "20"
                            }
                        },
                        label = { Text("기본", style = MaterialTheme.typography.caption3) }
                    )
                    CompactChip(
                        onClick = {
                            targetValue = when (selectedGoalType) {
                                GoalType.DAILY_STEPS, GoalType.WEEKLY_STEPS -> "10000"
                                GoalType.DAILY_CALORIES, GoalType.WEEKLY_CALORIES -> "500"
                                GoalType.DAILY_ACTIVE_MINUTES, GoalType.WEEKLY_ACTIVE_MINUTES -> "60"
                                GoalType.MONTHLY_WORKOUTS -> "30"
                            }
                        },
                        label = { Text("도전", style = MaterialTheme.typography.caption3) }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "취소"
                        )
                    }
                    Button(
                        onClick = {
                            val value = targetValue.toFloatOrNull() ?: getDefaultTargetValue(selectedGoalType)
                            onConfirm(selectedGoalType, value)
                        }
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_save),
                            contentDescription = "저장"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalTypeSelector(
    selectedType: GoalType,
    onTypeSelected: (GoalType) -> Unit
) {
    Column {
        GoalType.values().forEach { type ->
            CompactChip(
                onClick = { onTypeSelected(type) },
                label = { 
                    Text(
                        getGoalTypeName(type),
                        style = MaterialTheme.typography.caption2
                    )
                },
                colors = if (selectedType == type) {
                    ChipDefaults.primaryChipColors()
                } else {
                    ChipDefaults.chipColors()
                }
            )
        }
    }
}

private fun getGoalTypeName(type: GoalType): String {
    return when (type) {
        GoalType.DAILY_STEPS -> "일일 걸음"
        GoalType.WEEKLY_STEPS -> "주간 걸음"
        GoalType.DAILY_CALORIES -> "일일 칼로리"
        GoalType.WEEKLY_CALORIES -> "주간 칼로리"
        GoalType.DAILY_ACTIVE_MINUTES -> "일일 활동 시간"
        GoalType.WEEKLY_ACTIVE_MINUTES -> "주간 활동 시간"
        GoalType.MONTHLY_WORKOUTS -> "월간 운동 횟수"
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

private fun getDefaultTargetValue(type: GoalType): Float {
    return when (type) {
        GoalType.DAILY_STEPS -> 5000f
        GoalType.WEEKLY_STEPS -> 35000f
        GoalType.DAILY_CALORIES -> 300f
        GoalType.WEEKLY_CALORIES -> 2100f
        GoalType.DAILY_ACTIVE_MINUTES -> 30f
        GoalType.WEEKLY_ACTIVE_MINUTES -> 210f
        GoalType.MONTHLY_WORKOUTS -> 20f
    }
}