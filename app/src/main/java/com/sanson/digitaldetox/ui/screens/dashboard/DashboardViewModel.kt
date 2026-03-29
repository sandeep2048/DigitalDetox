package com.sanson.digitaldetox.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanson.digitaldetox.data.db.AppDatabase
import com.sanson.digitaldetox.data.model.AppUsageStats
import com.sanson.digitaldetox.data.model.DailyStats
import com.sanson.digitaldetox.data.repository.AppRepository
import com.sanson.digitaldetox.data.repository.UsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalTimeToday: Long = 0,
    val totalTimeWeek: Long = 0,
    val totalOpensToday: Int = 0,
    val mindfulExitsToday: Int = 0,
    val perAppStats: List<AppUsageStats> = emptyList(),
    val weeklyStats: List<DailyStats> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val appRepo = AppRepository(db.monitoredAppDao())
    private val usageRepo = UsageRepository(db.usageLogDao(), appRepo)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val totalTimeToday = usageRepo.getTotalTimeSpentToday()
                val totalTimeWeek = usageRepo.getTotalTimeSpentThisWeek()
                val totalOpens = usageRepo.getTotalOpensToday()
                val mindfulExits = usageRepo.getMindfulExitsToday()
                val perApp = usageRepo.getPerAppStats()
                val weekly = usageRepo.getWeeklyDailyStats()

                _uiState.value = DashboardUiState(
                    totalTimeToday = totalTimeToday,
                    totalTimeWeek = totalTimeWeek,
                    totalOpensToday = totalOpens,
                    mindfulExitsToday = mindfulExits,
                    perAppStats = perApp,
                    weeklyStats = weekly,
                    isLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
