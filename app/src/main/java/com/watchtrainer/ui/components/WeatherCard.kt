package com.watchtrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.watchtrainer.data.WeatherData
import com.watchtrainer.data.WeatherCondition

@Composable
fun WeatherCard(weatherData: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundPainter = CardDefaults.cardBackgroundPainter()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "현재 날씨",
                style = MaterialTheme.typography.caption1,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getWeatherEmoji(weatherData.condition),
                    style = MaterialTheme.typography.display3
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = "${weatherData.temperature.toInt()}°C",
                        style = MaterialTheme.typography.title2
                    )
                    Text(
                        text = weatherData.description,
                        style = MaterialTheme.typography.caption2,
                        color = MaterialTheme.colors.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "체감 ${weatherData.feelsLike.toInt()}°C | 습도 ${weatherData.humidity}%",
                style = MaterialTheme.typography.caption3,
                color = MaterialTheme.colors.onSurfaceVariant
            )
        }
    }
}

private fun getWeatherEmoji(condition: WeatherCondition): String {
    return when (condition) {
        WeatherCondition.CLEAR -> "☀️"
        WeatherCondition.CLOUDY -> "☁️"
        WeatherCondition.RAINY -> "🌧️"
        WeatherCondition.SNOWY -> "❄️"
        WeatherCondition.WINDY -> "💨"
        WeatherCondition.HOT -> "🔥"
        WeatherCondition.COLD -> "🥶"
    }
}