package com.watchtrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import com.watchtrainer.data.ExercisePlace
import com.watchtrainer.data.ExercisePlaceType
import com.watchtrainer.ui.components.PlaceCard
import com.watchtrainer.viewmodels.PlacesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PlacesScreen(
    navController: NavController,
    viewModel: PlacesViewModel = viewModel()
) {
    val recommendedPlaces by viewModel.recommendedPlaces.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val weatherData by viewModel.weatherData.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadRecommendations()
    }
    
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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
                        text = "추천 운동 장소",
                        style = MaterialTheme.typography.title2,
                        textAlign = TextAlign.Center
                    )
                }
                
                weatherData?.let { weather ->
                    item {
                        Card(
                            onClick = { /* No action */ },
                            modifier = Modifier.fillMaxWidth(),
                            backgroundPainter = CardDefaults.cardBackgroundPainter(
                                startBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                                endBackgroundColor = MaterialTheme.colors.surface
                            )
                        ) {
                            Text(
                                text = getWeatherBasedRecommendation(weather.temperature, weather.condition),
                                style = MaterialTheme.typography.caption1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
                
                if (recommendedPlaces.isEmpty()) {
                    item {
                        Text(
                            text = "주변에 추천할 운동 장소를 찾고 있습니다...",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(recommendedPlaces) { place ->
                        PlaceCard(
                            place = place,
                            onClick = {
                                // Navigate to map or detail view
                            }
                        )
                    }
                }
                
                item {
                    Button(
                        onClick = { viewModel.loadRecommendations() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("새로고침")
                    }
                }
            }
        }
    }
}

private fun getWeatherBasedRecommendation(temperature: Float, condition: com.watchtrainer.data.WeatherCondition): String {
    return when {
        temperature > 30 -> "🔥 더운 날씨예요. 실내 운동을 추천합니다!"
        temperature < 10 -> "🥶 추운 날씨예요. 실내에서 운동하세요!"
        condition == com.watchtrainer.data.WeatherCondition.RAINY -> "🌧️ 비가 와요. 실내 운동 장소를 추천합니다."
        condition == com.watchtrainer.data.WeatherCondition.CLEAR -> "☀️ 날씨가 좋아요! 야외 운동을 즐겨보세요!"
        else -> "운동하기 좋은 날씨예요! 💪"
    }
}