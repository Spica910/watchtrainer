package com.watchtrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

@Composable
fun AiMessageCard(message: String) {
    Card(
        onClick = { /* No action */ },
        modifier = Modifier.fillMaxWidth(),
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            startBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.2f),
            endBackgroundColor = MaterialTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🤖",
                    style = MaterialTheme.typography.title3
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "AI 코치",
                    style = MaterialTheme.typography.caption1
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center
            )
        }
    }
}