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

data class ExercisePlace(
    val id: String,
    val name: String,
    val address: String,
    val type: ExercisePlaceType,
    val isIndoor: Boolean,
    val distance: Float, // in meters
    val rating: Float = 0f, // 0-5 rating
    val latitude: Double,
    val longitude: Double, 
    val recommendationReason: String = "",
    val openingHours: String? = null,
    val facilities: List<String> = emptyList()
)

enum class ExercisePlaceType {
    PARK,
    GYM,
    TRAIL,
    STADIUM,
    POOL,
    CYCLE_PATH,
    OTHER
}