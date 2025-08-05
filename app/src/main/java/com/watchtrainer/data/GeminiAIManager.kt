package com.watchtrainer.data

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAIManager {
    
    companion object {
        private const val TAG = "GeminiAIManager"
        private const val API_KEY = "AIzaSyC0m9m_RCPLbyhGNu3NCS7RRxEIoxwnLQM" // 👈 여기에 API 키를 넣으세요!
        private const val MODEL_NAME = "gemini-2.5-flash"
    }
    
    private val generativeModel = GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = API_KEY
    )
    
    suspend fun generateWorkoutGuidance(
        healthData: HealthData,
        weatherData: WeatherData?,
        workoutState: WorkoutState,
        recommendedPlace: ExercisePlace? = null
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(healthData, weatherData, workoutState, recommendedPlace)
            
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )
            
            response.text ?: "화이팅! 오늘도 멋진 운동 하세요! 💪"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate AI guidance", e)
            getFallbackMessage(healthData, weatherData, workoutState)
        }
    }
    
    private fun buildPrompt(
        healthData: HealthData,
        weatherData: WeatherData?,
        workoutState: WorkoutState,
        recommendedPlace: ExercisePlace? = null
    ): String {
        val contextInfo = buildString {
            appendLine("당신은 재미있고 친근한 운동 코치입니다. 사용자를 격려하고 동기부여하는 메시지를 한국어로 작성해주세요.")
            appendLine("이모지를 적절히 사용해서 재미있게 표현해주세요.")
            appendLine()
            appendLine("현재 사용자 상태:")
            appendLine("- 오늘 걸음 수: ${healthData.steps}걸음")
            appendLine("- 현재 심박수: ${healthData.heartRate} BPM")
            appendLine("- 소모 칼로리: ${healthData.calories.toInt()} kcal")
            
            weatherData?.let {
                appendLine()
                appendLine("현재 날씨:")
                appendLine("- 온도: ${it.temperature}°C (체감 ${it.feelsLike}°C)")
                appendLine("- 날씨: ${it.description}")
                appendLine("- 습도: ${it.humidity}%")
            }
            
            appendLine()
            appendLine("운동 상태: ${when(workoutState) {
                WorkoutState.IDLE -> "운동 전"
                WorkoutState.ACTIVE -> "운동 중"
                WorkoutState.PAUSED -> "일시정지"
            }}")
            
            recommendedPlace?.let {
                appendLine()
                appendLine("추천 운동 장소:")
                appendLine("- 장소: ${it.name}")
                appendLine("- 거리: ${formatDistance(it.distance)}")
                appendLine("- 유형: ${if (it.isIndoor) "실내" else "야외"}")
            }
        }
        
        return when (workoutState) {
            WorkoutState.IDLE -> {
                "$contextInfo\n\n운동을 시작하도록 격려하는 메시지를 50자 이내로 작성해주세요. 날씨와 현재 상태를 고려해서 구체적인 운동을 추천해주세요."
            }
            WorkoutState.ACTIVE -> {
                "$contextInfo\n\n운동 중인 사용자를 응원하고 격려하는 메시지를 50자 이내로 작성해주세요. 심박수나 걸음 수를 언급하며 칭찬해주세요."
            }
            WorkoutState.PAUSED -> {
                "$contextInfo\n\n잠시 쉬고 있는 사용자가 다시 운동을 재개하도록 부드럽게 격려하는 메시지를 50자 이내로 작성해주세요."
            }
        }
    }
    
    private fun getFallbackMessage(
        healthData: HealthData,
        weatherData: WeatherData?,
        workoutState: WorkoutState
    ): String {
        return when (workoutState) {
            WorkoutState.IDLE -> {
                when {
                    healthData.steps < 3000 -> "오늘 ${healthData.steps}걸음이네요! 목표 달성을 위해 운동 시작해볼까요? 🚶"
                    healthData.steps < 7000 -> "좋아요! ${healthData.steps}걸음 달성! 조금만 더 하면 목표 달성이에요! 🏃"
                    else -> "와! 벌써 ${healthData.steps}걸음! 정말 대단해요! 👏"
                }
            }
            WorkoutState.ACTIVE -> {
                when {
                    healthData.heartRate > 140 -> "심박수 ${healthData.heartRate}! 열심히 하고 있네요! 화이팅! 🔥"
                    healthData.heartRate > 100 -> "좋은 페이스예요! 이대로 계속 가요! 💪"
                    else -> "천천히 시작해도 괜찮아요! 꾸준함이 중요해요! 😊"
                }
            }
            WorkoutState.PAUSED -> {
                "잠깐 쉬는 것도 운동의 일부예요! 준비되면 다시 시작해요! 🌟"
            }
        }
    }
    
    suspend fun generateWorkoutTip(workoutType: WorkoutType): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                ${workoutType.name} 운동에 대한 재미있고 유용한 팁을 한국어로 30자 이내로 알려주세요.
                이모지를 포함해서 친근하게 작성해주세요.
            """.trimIndent()
            
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )
            
            response.text ?: getDefaultTip(workoutType)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate workout tip", e)
            getDefaultTip(workoutType)
        }
    }
    
    private fun getDefaultTip(workoutType: WorkoutType): String {
        return when (workoutType) {
            WorkoutType.WALKING -> "걸을 때는 팔을 크게 흔들어보세요! 🚶"
            WorkoutType.RUNNING -> "호흡은 코로 들이쉬고 입으로 내쉬세요! 🏃"
            WorkoutType.CYCLING -> "페달링은 일정한 속도로 유지해요! 🚴"
            WorkoutType.STRENGTH -> "근육에 집중하며 천천히 움직여요! 💪"
            WorkoutType.YOGA -> "호흡과 함께 몸의 긴장을 풀어요! 🧘"
            WorkoutType.OTHER -> "자신만의 페이스를 찾아가세요! ⭐"
        }
    }
    
    suspend fun generateMotivationalQuote(): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                운동 동기부여가 되는 짧고 재미있는 명언을 한국어로 만들어주세요.
                30자 이내로 작성하고, 이모지를 포함해주세요.
                너무 진부하지 않고 재미있게 만들어주세요.
            """.trimIndent()
            
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )
            
            response.text ?: getRandomMotivationalQuote()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate motivational quote", e)
            getRandomMotivationalQuote()
        }
    }
    
    private fun getRandomMotivationalQuote(): String {
        val quotes = listOf(
            "오늘의 땀방울이 내일의 자신감! 💦",
            "시작이 반이다! 나머지 반도 해보자! 🌟",
            "운동은 미래의 나에게 주는 선물! 🎁",
            "한 걸음 한 걸음이 변화의 시작! 👣",
            "포기하지 마! 시작한 너를 기억해! 🔥"
        )
        return quotes.random()
    }
    
    private fun formatDistance(meters: Float): String {
        return when {
            meters < 1000 -> "${meters.toInt()}m"
            else -> String.format("%.1fkm", meters / 1000)
        }
    }
}