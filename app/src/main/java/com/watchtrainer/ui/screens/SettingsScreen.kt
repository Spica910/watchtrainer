package com.watchtrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.*
import com.watchtrainer.viewmodels.MainViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
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
                    text = "설정",
                    style = MaterialTheme.typography.title2,
                    textAlign = TextAlign.Center
                )
            }
            
            item {
                ToggleChip(
                    modifier = Modifier.fillMaxWidth(),
                    checked = viewModel.isVoiceEnabled,
                    onCheckedChange = { viewModel.toggleVoiceFeedback() },
                    label = {
                        Text("음성 피드백", style = MaterialTheme.typography.body2)
                    },
                    toggleControl = {
                        Icon(
                            imageVector = ToggleChipDefaults.switchIcon(viewModel.isVoiceEnabled),
                            contentDescription = if (viewModel.isVoiceEnabled) "켜짐" else "꺼짐"
                        )
                    }
                )
            }
            
            item {
                Text(
                    text = if (viewModel.isVoiceEnabled) 
                        "운동 중 음성 안내가 제공됩니다" 
                    else 
                        "음성 안내가 꺼져 있습니다",
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Card(
                    onClick = { /* No action */ },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundPainter = CardDefaults.cardBackgroundPainter()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "음성 피드백 기능",
                            style = MaterialTheme.typography.caption1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• 운동 시작/종료 안내\n• AI 코치 메시지 음성 출력\n• 목표 달성 알림\n• 운동 상태 업데이트",
                            style = MaterialTheme.typography.caption2,
                            color = MaterialTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }
            
            item {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* Open about dialog */ },
                    label = { Text("앱 정보") },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }
    }
}