package com.watchtrainer.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.watchtrainer.data.WorkoutSession
import com.watchtrainer.data.WorkoutType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Database(
    entities = [WorkoutEntity::class, WorkoutGoalEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {
    
    abstract fun workoutDao(): WorkoutDao
    abstract fun goalDao(): WorkoutGoalDao
    
    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null
        
        fun getInstance(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey
    val id: String,
    val startTime: Long,
    val endTime: Long?,
    val totalSteps: Int,
    val averageHeartRate: Int,
    val caloriesBurned: Float,
    val distance: Float,
    val workoutType: WorkoutType,
    val duration: Long = 0L, // in milliseconds
    val maxHeartRate: Int = 0,
    val minHeartRate: Int = 0,
    val notes: String? = null
)

@Entity(tableName = "workout_goals")
data class WorkoutGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalType: GoalType,
    val targetValue: Float,
    val currentValue: Float = 0f,
    val deadline: Date?,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val completedAt: Date? = null
)

enum class GoalType {
    DAILY_STEPS,
    WEEKLY_STEPS,
    DAILY_CALORIES,
    WEEKLY_CALORIES,
    DAILY_ACTIVE_MINUTES,
    WEEKLY_ACTIVE_MINUTES,
    MONTHLY_WORKOUTS
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>
    
    @Query("SELECT * FROM workouts WHERE startTime >= :startTime AND startTime <= :endTime ORDER BY startTime DESC")
    suspend fun getWorkoutsBetween(startTime: Long, endTime: Long): List<WorkoutEntity>
    
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkout(workoutId: String): WorkoutEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)
    
    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)
    
    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
    
    @Query("SELECT COUNT(*) FROM workouts WHERE startTime >= :startTime")
    suspend fun getWorkoutCountSince(startTime: Long): Int
    
    @Query("SELECT SUM(totalSteps) FROM workouts WHERE startTime >= :startTime")
    suspend fun getTotalStepsSince(startTime: Long): Int?
    
    @Query("SELECT SUM(caloriesBurned) FROM workouts WHERE startTime >= :startTime")
    suspend fun getTotalCaloriesSince(startTime: Long): Float?
    
    @Query("SELECT SUM(duration) FROM workouts WHERE startTime >= :startTime")
    suspend fun getTotalDurationSince(startTime: Long): Long?
}

@Dao
interface WorkoutGoalDao {
    @Query("SELECT * FROM workout_goals WHERE isActive = 1")
    fun getActiveGoals(): Flow<List<WorkoutGoalEntity>>
    
    @Query("SELECT * FROM workout_goals WHERE goalType = :type AND isActive = 1")
    suspend fun getActiveGoalByType(type: GoalType): WorkoutGoalEntity?
    
    @Insert
    suspend fun insertGoal(goal: WorkoutGoalEntity)
    
    @Update
    suspend fun updateGoal(goal: WorkoutGoalEntity)
    
    @Query("UPDATE workout_goals SET currentValue = :value WHERE id = :goalId")
    suspend fun updateGoalProgress(goalId: Long, value: Float)
    
    @Query("UPDATE workout_goals SET isActive = 0, completedAt = :completedAt WHERE id = :goalId")
    suspend fun completeGoal(goalId: Long, completedAt: Date)
}

class Converters {
    @TypeConverter
    fun fromWorkoutType(type: WorkoutType): String = type.name
    
    @TypeConverter
    fun toWorkoutType(type: String): WorkoutType = WorkoutType.valueOf(type)
    
    @TypeConverter
    fun fromGoalType(type: GoalType): String = type.name
    
    @TypeConverter
    fun toGoalType(type: String): GoalType = GoalType.valueOf(type)
    
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time
    
    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}