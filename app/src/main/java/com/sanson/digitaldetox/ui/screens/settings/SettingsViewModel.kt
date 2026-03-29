package com.sanson.digitaldetox.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanson.digitaldetox.util.PreferenceManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = PreferenceManager(application)

    val isServiceEnabled = prefs.isServiceEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val cooldownSeconds = prefs.cooldownSeconds
        .stateIn(viewModelScope, SharingStarted.Lazily, 8)

    val isEscalationEnabled = prefs.isEscalationEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isLateNightModeEnabled = prefs.isLateNightModeEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val lateNightStart = prefs.lateNightStart
        .stateIn(viewModelScope, SharingStarted.Lazily, 22)

    val lateNightEnd = prefs.lateNightEnd
        .stateIn(viewModelScope, SharingStarted.Lazily, 6)

    val isSessionNudgeEnabled = prefs.isSessionNudgeEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val sessionNudgeMinutes = prefs.sessionNudgeMinutes
        .stateIn(viewModelScope, SharingStarted.Lazily, 5)

    fun setServiceEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setServiceEnabled(enabled) }
    }

    fun setCooldownSeconds(seconds: Int) {
        viewModelScope.launch { prefs.setCooldownSeconds(seconds) }
    }

    fun setEscalationEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setEscalationEnabled(enabled) }
    }

    fun setLateNightMode(enabled: Boolean) {
        viewModelScope.launch { prefs.setLateNightMode(enabled) }
    }

    fun setLateNightStart(hour: Int) {
        viewModelScope.launch { prefs.setLateNightStart(hour) }
    }

    fun setLateNightEnd(hour: Int) {
        viewModelScope.launch { prefs.setLateNightEnd(hour) }
    }

    fun setSessionNudgeEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setSessionNudgeEnabled(enabled) }
    }

    fun setSessionNudgeMinutes(minutes: Int) {
        viewModelScope.launch { prefs.setSessionNudgeMinutes(minutes) }
    }
}
