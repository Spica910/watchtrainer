package com.watchtrainer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.watchtrainer.data.WorkoutSession
import com.watchtrainer.data.repository.StatsPeriod
import com.watchtrainer.data.repository.WorkoutRepository
import com.watchtrainer.data.repository.WorkoutStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = WorkoutRepository(application)
    
    private val _workouts = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val workouts: StateFlow<List<WorkoutSession>> = _workouts.asStateFlow()
    
    private val _currentStats = MutableStateFlow<WorkoutStats?>(null)
    val currentStats: StateFlow<WorkoutStats?> = _currentStats.asStateFlow()
    
    init {
        loadWorkouts()
        loadStats(StatsPeriod.WEEK)
    }
    
    private fun loadWorkouts() {
        viewModelScope.launch {
            repository.getAllWorkouts().collect { workoutList ->
                _workouts.value = workoutList
            }
        }
    }
    
    fun loadStats(period: StatsPeriod) {
        viewModelScope.launch {
            _currentStats.value = repository.getWorkoutStats(period)
        }
    }
}