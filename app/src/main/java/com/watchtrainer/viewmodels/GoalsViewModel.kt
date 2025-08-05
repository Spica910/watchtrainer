package com.watchtrainer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.watchtrainer.data.database.GoalType
import com.watchtrainer.data.database.WorkoutGoalEntity
import com.watchtrainer.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class GoalsViewModel(application: Application) : AndroidViewModel(application) {
    
    val repository = WorkoutRepository(application)
    
    private val _activeGoals = MutableStateFlow<List<WorkoutGoalEntity>>(emptyList())
    val activeGoals: StateFlow<List<WorkoutGoalEntity>> = _activeGoals.asStateFlow()
    
    init {
        loadActiveGoals()
    }
    
    private fun loadActiveGoals() {
        viewModelScope.launch {
            repository.getActiveGoals().collect { goals ->
                _activeGoals.value = goals
            }
        }
    }
    
    fun createGoal(goalType: GoalType, targetValue: Float) {
        viewModelScope.launch {
            repository.createGoal(goalType, targetValue)
        }
    }
    
    fun completeGoal(goal: WorkoutGoalEntity) {
        viewModelScope.launch {
            repository.goalDao.completeGoal(goal.id, Date())
        }
    }
}