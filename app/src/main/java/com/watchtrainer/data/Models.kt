package com.watchtrainer.data

data class HealthData(
    val steps: Int = 0,
    val heartRate: Int = 0,
    val calories: Float = 0f,
    val distance: Float = 0f,
    val activeTime: Int = 0, // in minutes
    val lastUpdated: Long = System.currentTimeMillis()
)

data class WeatherData(
    val temperature: Float = 0f,
    val description: String = "",
    val humidity: Int = 0,
    val windSpeed: Float = 0f,
    val feelsLike: Float = 0f,
    val condition: WeatherCondition = WeatherCondition.CLEAR
)

enum class WeatherCondition {
    CLEAR,
    CLOUDY,
    RAINY,
    SNOWY,
    WINDY,
    HOT,
    COLD
}

enum class WorkoutState {
    IDLE,
    ACTIVE,
    PAUSED
}

data class WorkoutSession(
    val id: String,
    val startTime: Long,
    val endTime: Long? = null,
    val totalSteps: Int = 0,
    val averageHeartRate: Int = 0,
    val caloriesBurned: Float = 0f,
    val distance: Float = 0f,
    val workoutType: WorkoutType = WorkoutType.WALKING
)

enum class WorkoutType {
    WALKING,
    RUNNING,
    CYCLING,
    STRENGTH,
    YOGA,
    OTHER
}

data class AiCoachMessage(
    val message: String,
    val motivation: String,
    val tip: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)