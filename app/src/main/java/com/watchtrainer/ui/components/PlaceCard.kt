package com.watchtrainer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.watchtrainer.data.ExercisePlace
import com.watchtrainer.data.ExercisePlaceType

@Composable
fun PlaceCard(
    place: ExercisePlace,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        backgroundPainter = CardDefaults.cardBackgroundPainter()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getPlaceTypeEmoji(place.type),
                            style = MaterialTheme.typography.title3
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.body2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Text(
                        text = place.address,
                        style = MaterialTheme.typography.caption2,
                        color = MaterialTheme.colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Indoor/Outdoor indicator
                Chip(
                    onClick = { /* No action needed for indicator */ },
                    label = { 
                        Text(
                            if (place.isIndoor) "실내" else "야외",
                            style = MaterialTheme.typography.caption3
                        )
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = if (place.isIndoor) 
                            MaterialTheme.colors.secondary.copy(alpha = 0.2f)
                        else 
                            MaterialTheme.colors.primary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.height(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Distance
                Text(
                    text = formatDistance(place.distance),
                    style = MaterialTheme.typography.caption1,
                    color = MaterialTheme.colors.primary
                )
                
                // Rating
                if (place.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐",
                            style = MaterialTheme.typography.caption2
                        )
                        Text(
                            text = String.format("%.1f", place.rating),
                            style = MaterialTheme.typography.caption1
                        )
                    }
                }
            }
            
            if (place.recommendationReason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = place.recommendationReason,
                    style = MaterialTheme.typography.caption3,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

private fun getPlaceTypeEmoji(type: ExercisePlaceType): String {
    return when (type) {
        ExercisePlaceType.PARK -> "🌳"
        ExercisePlaceType.GYM -> "💪"
        ExercisePlaceType.TRAIL -> "🥾"
        ExercisePlaceType.STADIUM -> "🏟️"
        ExercisePlaceType.POOL -> "🏊"
        ExercisePlaceType.CYCLE_PATH -> "🚴"
        ExercisePlaceType.OTHER -> "📍"
    }
}

private fun formatDistance(meters: Float): String {
    return when {
        meters < 1000 -> "${meters.toInt()}m"
        else -> String.format("%.1fkm", meters / 1000)
    }
}