package com.watchtrainer.data.repository

import android.content.Context
import com.watchtrainer.data.WorkoutSession
import com.watchtrainer.data.WorkoutType
import com.watchtrainer.data.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

class WorkoutRepository(context: Context) {
    
    private val database = WorkoutDatabase.getInstance(context)
    val workoutDao = database.workoutDao()
    val goalDao = database.goalDao()
    
    fun getAllWorkouts(): Flow<List<WorkoutSession>> {
        return workoutDao.getAllWorkouts().map { entities ->
            entities.map { entity ->
                WorkoutSession(
                    id = entity.id,
                    startTime = entity.startTime,
                    endTime = entity.endTime,
                    totalSteps = entity.totalSteps,
                    averageHeartRate = entity.averageHeartRate,
                    caloriesBurned = entity.caloriesBurned,
                    distance = entity.distance,
                    workoutType = entity.workoutType
                )
            }
        }
    }
    
    suspend fun saveWorkout(workout: WorkoutSession) = withContext(Dispatchers.IO) {
        val entity = WorkoutEntity(
            id = workout.id,
            startTime = workout.startTime,
            endTime = workout.endTime,
            totalSteps = workout.totalSteps,
            averageHeartRate = workout.averageHeartRate,
            caloriesBurned = workout.caloriesBurned,
            distance = workout.distance,
            workoutType = workout.workoutType,
            duration = (workout.endTime ?: workout.startTime) - workout.startTime
        )
        workoutDao.insertWorkout(entity)
        
        // Update goals progress
        updateGoalsProgress()
    }
    
    suspend fun getWorkoutStats(period: StatsPeriod): WorkoutStats = withContext(Dispatchers.IO) {
        val startTime = when (period) {
            StatsPeriod.TODAY -> getTodayStart()
            StatsPeriod.WEEK -> getWeekStart()
            StatsPeriod.MONTH -> getMonthStart()
            StatsPeriod.ALL_TIME -> 0L
        }
        
        val workouts = workoutDao.getWorkoutsBetween(startTime, System.currentTimeMillis())
        val totalSteps = workoutDao.getTotalStepsSince(startTime) ?: 0
        val totalCalories = workoutDao.getTotalCaloriesSince(startTime) ?: 0f
        val totalDuration = workoutDao.getTotalDurationSince(startTime) ?: 0L
        val workoutCount = workouts.size
        
        WorkoutStats(
            period = period,
            totalWorkouts = workoutCount,
            totalSteps = totalSteps,
            totalCalories = totalCalories,
            totalDuration = totalDuration,
            averageWorkoutDuration = if (workoutCount > 0) totalDuration / workoutCount else 0L,
            mostFrequentWorkoutType = findMostFrequentType(workouts)
        )
    }
    
    suspend fun getWeeklyProgress(): List<DailyProgress> = withContext(Dispatchers.IO) {
        val weekStart = getWeekStart()
        val dailyProgress = mutableListOf<DailyProgress>()
        
        for (i in 0..6) {
            val dayStart = weekStart + (i * 24 * 60 * 60 * 1000)
            val dayEnd = dayStart + (24 * 60 * 60 * 1000) - 1
            
            val dayWorkouts = workoutDao.getWorkoutsBetween(dayStart, dayEnd)
            val steps = dayWorkouts.sumOf { it.totalSteps }
            val calories = dayWorkouts.sumOf { it.caloriesBurned.toDouble() }.toFloat()
            val duration = dayWorkouts.sumOf { it.duration }
            
            dailyProgress.add(
                DailyProgress(
                    date = Date(dayStart),
                    steps = steps,
                    calories = calories,
                    activeMinutes = (duration / 60000).toInt(),
                    workoutCount = dayWorkouts.size
                )
            )
        }
        
        dailyProgress
    }
    
    // Goals management
    fun getActiveGoals(): Flow<List<WorkoutGoalEntity>> {
        return goalDao.getActiveGoals()
    }
    
    suspend fun createGoal(goalType: GoalType, targetValue: Float, deadline: Date? = null) {
        val goal = WorkoutGoalEntity(
            goalType = goalType,
            targetValue = targetValue,
            deadline = deadline
        )
        goalDao.insertGoal(goal)
    }
    
    suspend fun updateGoalsProgress() = withContext(Dispatchers.IO) {
        val activeGoals = goalDao.getActiveGoals().first()
        
        for (goal in activeGoals) {
            val currentValue = when (goal.goalType) {
                GoalType.DAILY_STEPS -> workoutDao.getTotalStepsSince(getTodayStart())?.toFloat() ?: 0f
                GoalType.WEEKLY_STEPS -> workoutDao.getTotalStepsSince(getWeekStart())?.toFloat() ?: 0f
                GoalType.DAILY_CALORIES -> workoutDao.getTotalCaloriesSince(getTodayStart()) ?: 0f
                GoalType.WEEKLY_CALORIES -> workoutDao.getTotalCaloriesSince(getWeekStart()) ?: 0f
                GoalType.DAILY_ACTIVE_MINUTES -> (workoutDao.getTotalDurationSince(getTodayStart()) ?: 0L) / 60000f
                GoalType.WEEKLY_ACTIVE_MINUTES -> (workoutDao.getTotalDurationSince(getWeekStart()) ?: 0L) / 60000f
                GoalType.MONTHLY_WORKOUTS -> workoutDao.getWorkoutCountSince(getMonthStart()).toFloat()
            }
            
            goalDao.updateGoalProgress(goal.id, currentValue)
            
            // Check if goal is completed
            if (currentValue >= goal.targetValue) {
                goalDao.completeGoal(goal.id, Date())
            }
        }
    }
    
    private fun getTodayStart(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    private fun getWeekStart(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    private fun getMonthStart(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    private fun findMostFrequentType(workouts: List<WorkoutEntity>): WorkoutType? {
        return workouts.groupBy { it.workoutType }
            .maxByOrNull { it.value.size }
            ?.key
    }
}

enum class StatsPeriod {
    TODAY,
    WEEK,
    MONTH,
    ALL_TIME
}

data class WorkoutStats(
    val period: StatsPeriod,
    val totalWorkouts: Int,
    val totalSteps: Int,
    val totalCalories: Float,
    val totalDuration: Long,
    val averageWorkoutDuration: Long,
    val mostFrequentWorkoutType: WorkoutType?
)

data class DailyProgress(
    val date: Date,
    val steps: Int,
    val calories: Float,
    val activeMinutes: Int,
    val workoutCount: Int
)