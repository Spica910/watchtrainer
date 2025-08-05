package com.watchtrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.watchtrainer.data.HealthData

@Composable
fun HealthDataCard(healthData: HealthData) {
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
                text = "오늘의 활동",
                style = MaterialTheme.typography.caption1,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HealthMetric(
                    value = healthData.steps.toString(),
                    label = "걸음"
                )
                HealthMetric(
                    value = "${healthData.heartRate}",
                    label = "심박수"
                )
                HealthMetric(
                    value = "${healthData.calories.toInt()}",
                    label = "칼로리"
                )
            }
        }
    }
}

@Composable
private fun HealthMetric(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.title3
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.onSurfaceVariant
        )
    }
}