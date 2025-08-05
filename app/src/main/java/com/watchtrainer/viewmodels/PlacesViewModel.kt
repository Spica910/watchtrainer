package com.watchtrainer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.watchtrainer.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlacesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val placeRecommendationManager = PlaceRecommendationManager(application)
    private val weatherDataManager = WeatherDataManager(application)
    
    private val _recommendedPlaces = MutableStateFlow<List<ExercisePlace>>(emptyList())
    val recommendedPlaces: StateFlow<List<ExercisePlace>> = _recommendedPlaces.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData.asStateFlow()
    
    // Get current workout type from SharedPreferences or default
    private var currentWorkoutType: WorkoutType = WorkoutType.WALKING
    
    fun loadRecommendations(workoutType: WorkoutType = currentWorkoutType) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get current weather
                val weather = weatherDataManager.getCurrentWeather()
                _weatherData.value = weather
                
                // Get location and recommended places
                val places = placeRecommendationManager.getRecommendedPlaces(
                    weather = weather,
                    workoutType = workoutType
                )
                
                _recommendedPlaces.value = places
                currentWorkoutType = workoutType
            } catch (e: Exception) {
                // Handle error
                _recommendedPlaces.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun setWorkoutType(type: WorkoutType) {
        currentWorkoutType = type
        loadRecommendations(type)
    }
}