package com.watchtrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

@Composable
fun CircularMetric(
    value: String,
    label: String,
    progress: Float
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 4.dp,
            indicatorColor = MaterialTheme.colors.primary,
            trackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.title3
            )
            Text(
                text = label,
                style = MaterialTheme.typography.caption3,
                color = MaterialTheme.colors.onSurfaceVariant
            )
        }
    }
}