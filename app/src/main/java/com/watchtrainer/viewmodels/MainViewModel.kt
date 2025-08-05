package com.watchtrainer.viewmodels

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import com.watchtrainer.data.*
import com.watchtrainer.data.repository.WorkoutRepository
import com.watchtrainer.services.ExerciseService
import com.watchtrainer.utils.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    val listState = ScalingLazyListState()
    
    private val context = application.applicationContext
    private val healthDataManager = HealthDataManager(context)
    private val weatherDataManager = WeatherDataManager(context)
    private val geminiAIManager = GeminiAIManager()
    private val workoutRepository = WorkoutRepository(context)
    private val ttsManager = TextToSpeechManager(context)
    
    private var currentWorkoutSession: WorkoutSession? = null
    private var workoutStartTime: Long = 0L
    
    var isVoiceEnabled by mutableStateOf(true)
        private set
    
    var currentWorkoutType by mutableStateOf(WorkoutType.WALKING)
        private set
    
    private val _workoutState = MutableStateFlow(WorkoutState.IDLE)
    val workoutState: StateFlow<WorkoutState> = _workoutState.asStateFlow()
    
    private val _healthData = MutableStateFlow(HealthData())
    val healthData: StateFlow<HealthData> = _healthData.asStateFlow()
    
    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData.asStateFlow()
    
    private val _aiMessage = MutableStateFlow("")
    val aiMessage: StateFlow<String> = _aiMessage.asStateFlow()
    
    var isLoading by mutableStateOf(false)
        private set
    
    init {
        healthDataManager.initialize()
        loadInitialData()
        observeHealthData()
        observeWeatherData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            // Load today's health data
            val steps = healthDataManager.getTodaySteps()
            val heartRate = healthDataManager.getLatestHeartRate()
            val calories = healthDataManager.getTodayCalories()
            
            _healthData.value = HealthData(
                steps = steps,
                heartRate = heartRate,
                calories = calories
            )
            
            // Load weather data
            try {
                val weather = weatherDataManager.getCurrentWeather()
                _weatherData.value = weather
                
                // Get weather-based recommendation
                val recommendation = weatherDataManager.getWeatherRecommendation(weather)
                _aiMessage.value = recommendation
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private fun observeHealthData() {
        viewModelScope.launch {
            healthDataManager.observeHealthData().collect { data ->
                _healthData.value = data
            }
        }
    }
    
    private fun observeWeatherData() {
        viewModelScope.launch {
            weatherDataManager.getWeatherFlow().collect { data ->
                _weatherData.value = data
            }
        }
    }
    
    fun startWorkout() {
        viewModelScope.launch {
            _workoutState.value = WorkoutState.ACTIVE
            workoutStartTime = System.currentTimeMillis()
            
            // Create new workout session
            currentWorkoutSession = WorkoutSession(
                id = UUID.randomUUID().toString(),
                startTime = workoutStartTime,
                workoutType = currentWorkoutType
            )
            
            // Start exercise service
            val intent = Intent(context, ExerciseService::class.java)
            context.startForegroundService(intent)
            
            // Voice feedback
            if (isVoiceEnabled) {
                ttsManager.speakWorkoutStart()
            }
            
            // Generate AI motivation for starting
            requestAiGuidance()
        }
    }
    
    fun stopWorkout() {
        viewModelScope.launch {
            _workoutState.value = WorkoutState.PAUSED
            // Voice feedback
            if (isVoiceEnabled) {
                ttsManager.speakWorkoutPause()
            }
        }
    }
    
    fun resumeWorkout() {
        viewModelScope.launch {
            _workoutState.value = WorkoutState.ACTIVE
            // Voice feedback
            if (isVoiceEnabled) {
                ttsManager.speakWorkoutResume()
            }
            // Generate AI motivation for resuming
            requestAiGuidance()
        }
    }
    
    fun endWorkout() {
        viewModelScope.launch {
            _workoutState.value = WorkoutState.IDLE
            
            // Save workout session
            currentWorkoutSession?.let { session ->
                val endTime = System.currentTimeMillis()
                val duration = endTime - session.startTime
                val completedSession = session.copy(
                    endTime = endTime,
                    totalSteps = _healthData.value.steps,
                    averageHeartRate = _healthData.value.heartRate,
                    caloriesBurned = _healthData.value.calories,
                    distance = _healthData.value.distance
                )
                workoutRepository.saveWorkout(completedSession)
                
                // Voice feedback
                if (isVoiceEnabled) {
                    val durationStr = formatDuration(duration)
                    ttsManager.speakWorkoutEnd(
                        durationStr,
                        completedSession.totalSteps,
                        completedSession.caloriesBurned.toInt()
                    )
                }
            }
            
            // Stop exercise service
            val intent = Intent(context, ExerciseService::class.java)
            context.stopService(intent)
            
            // Generate workout summary
            generateWorkoutSummary()
            
            // Reset current session
            currentWorkoutSession = null
        }
    }
    
    private fun formatDuration(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        
        return if (hours > 0) {
            "${hours}시간 ${minutes}분"
        } else {
            "${minutes}분"
        }
    }
    
    private suspend fun generateWorkoutSummary() {
        try {
            val message = geminiAIManager.generateWorkoutGuidance(
                _healthData.value,
                _weatherData.value,
                WorkoutState.IDLE
            )
            _aiMessage.value = "운동 완료! $message"
        } catch (e: Exception) {
            _aiMessage.value = "수고하셨어요! 오늘도 멋진 운동이었어요! 💪"
        }
    }
    
    fun updateHealthData(data: HealthData) {
        _healthData.value = data
    }
    
    fun updateWeatherData(data: WeatherData) {
        _weatherData.value = data
    }
    
    fun updateAiMessage(message: String) {
        _aiMessage.value = message
    }
    
    fun requestAiGuidance() {
        viewModelScope.launch {
            isLoading = true
            try {
                val message = geminiAIManager.generateWorkoutGuidance(
                    _healthData.value,
                    _weatherData.value,
                    _workoutState.value
                )
                _aiMessage.value = message
                
                // Voice feedback for AI message
                if (isVoiceEnabled && _workoutState.value == WorkoutState.ACTIVE) {
                    ttsManager.speakMotivation(message)
                }
            } catch (e: Exception) {
                // Fallback message
                _aiMessage.value = when (_workoutState.value) {
                    WorkoutState.IDLE -> "운동 시작해볼까요? 오늘도 화이팅! 💪"
                    WorkoutState.ACTIVE -> "잘하고 있어요! 계속 힘내세요! 🔥"
                    WorkoutState.PAUSED -> "잠시 쉬는 것도 좋아요! 준비되면 다시 시작해요! 😊"
                }
            } finally {
                isLoading = false
            }
        }
    }
    
    fun toggleVoiceFeedback() {
        isVoiceEnabled = !isVoiceEnabled
        if (!isVoiceEnabled) {
            ttsManager.stop()
        }
    }
    
    fun setWorkoutType(type: WorkoutType) {
        currentWorkoutType = type
        currentWorkoutSession?.let { session ->
            currentWorkoutSession = session.copy(workoutType = type)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        healthDataManager.disconnect()
        ttsManager.shutdown()
    }
}