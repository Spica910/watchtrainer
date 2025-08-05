package com.watchtrainer.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.watchtrainer.MainActivity
import com.watchtrainer.R
import com.watchtrainer.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class ExerciseService : Service() {
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "exercise_tracking"
        private const val TAG = "ExerciseService"
    }
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var healthDataManager: HealthDataManager
    private lateinit var weatherDataManager: WeatherDataManager
    private lateinit var geminiAIManager: GeminiAIManager
    private lateinit var vibrator: Vibrator
    
    private var lastMotivationTime = 0L
    private val MOTIVATION_INTERVAL = 5 * 60 * 1000L // 5 minutes
    
    override fun onCreate() {
        super.onCreate()
        healthDataManager = HealthDataManager(this)
        weatherDataManager = WeatherDataManager(this)
        geminiAIManager = GeminiAIManager()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        
        createNotificationChannel()
        healthDataManager.initialize()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification("운동 시작!", "화이팅! 💪"))
        
        serviceScope.launch {
            // Collect health data
            launch {
                healthDataManager.observeHealthData().collectLatest { healthData ->
                    updateNotification(healthData)
                    checkAndSendMotivation(healthData)
                }
            }
            
            // Periodic AI guidance
            launch {
                while (isActive) {
                    delay(MOTIVATION_INTERVAL)
                    sendAIMotivation()
                }
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        healthDataManager.disconnect()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "운동 추적",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "운동 중 상태를 표시합니다"
            setShowBadge(false)
        }
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun createNotification(title: String, text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateNotification(healthData: HealthData) {
        val notification = createNotification(
            "운동 중",
            "❤️ ${healthData.heartRate} BPM | 👣 ${healthData.steps} 걸음 | 🔥 ${healthData.calories.toInt()} kcal"
        )
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private suspend fun checkAndSendMotivation(healthData: HealthData) {
        val currentTime = System.currentTimeMillis()
        
        // Send motivation based on milestones
        when {
            healthData.steps == 1000 -> sendMotivationNotification("천 걸음 달성! 🎉")
            healthData.steps == 5000 -> sendMotivationNotification("5천 걸음! 대단해요! 🌟")
            healthData.steps == 10000 -> sendMotivationNotification("만보 달성! 축하해요! 🎊")
            healthData.calories.toInt() == 100 -> sendMotivationNotification("100 칼로리 소모! 🔥")
            healthData.heartRate > 150 && currentTime - lastMotivationTime > 60000 -> {
                sendMotivationNotification("심박수가 높아요! 페이스 조절하세요! 💗")
                lastMotivationTime = currentTime
            }
        }
    }
    
    private suspend fun sendAIMotivation() {
        try {
            val healthData = healthDataManager.getTodaySteps().let { steps ->
                HealthData(
                    steps = steps,
                    heartRate = healthDataManager.getLatestHeartRate(),
                    calories = healthDataManager.getTodayCalories()
                )
            }
            
            val weatherData = weatherDataManager.getCurrentWeather()
            val message = geminiAIManager.generateWorkoutGuidance(
                healthData,
                weatherData,
                WorkoutState.ACTIVE
            )
            
            sendMotivationNotification(message)
        } catch (e: Exception) {
            // Fallback to simple motivation
            val quote = geminiAIManager.generateMotivationalQuote()
            sendMotivationNotification(quote)
        }
    }
    
    private fun sendMotivationNotification(message: String) {
        // Vibrate pattern: wait 0ms, vibrate 200ms, wait 100ms, vibrate 200ms
        val pattern = longArrayOf(0, 200, 100, 200)
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        }
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AI 코치")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}